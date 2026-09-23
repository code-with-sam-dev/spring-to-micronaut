#!/usr/bin/env bash
# Every claim in the video, measured. Each mistake in mistakes/ is laid over a
# scratch copy of the app, never the app itself, and each case asserts what it
# expects and fails rather than printing something plausible.
#
#   mistake                Spring Boot                Micronaut
#   private @Transactional builds, row still written  build fails
#   findByCurrencyy        builds, refuses to start   build fails
#   missing Gateway bean   refuses to start           starts, first request 500
#   + eagerInitSingletons                             refuses to start
#
# Needs Postgres from compose.yaml: docker compose up -d
set -euo pipefail
cd "$(dirname "$0")/.."
ROOT=$(pwd)
export JAVA_HOME="${JAVA_HOME_25:-$HOME/.sdkman/candidates/java/25.0.4-amzn}"
JAVA="$JAVA_HOME/bin/java"
WORK=$(mktemp -d)
PIDS=()
# VERIFY_KEEP=<dir> keeps every log, so the terminal frames in the video are
# the strings these tools printed, copied rather than retyped.
KEEP="${VERIFY_KEEP:-}"
cleanup() {
  for p in "${PIDS[@]:-}"; do kill "$p" 2>/dev/null || true; done
  if [ -n "$KEEP" ]; then mkdir -p "$KEEP" && cp "$WORK"/*.log "$KEEP"/ 2>/dev/null || true; fi
  rm -rf "$WORK"
}
trap cleanup EXIT

fail() { echo "CLAIM FAILED: $*" >&2; exit 1; }
# copy <framework> <mistake...>: a fresh copy of the app with overlays applied
copy() {
  local fw=$1; shift
  local dir="$WORK/$fw-$(date +%s%N)"
  cp -R "$ROOT/$fw-payments" "$dir"
  rm -rf "$dir/target"
  for m in "$@"; do cp -R "$ROOT/mistakes/$m/$fw/." "$dir/"; done
  echo "$dir"
}
jar() { ls "$1"/target/*-payments-*.jar | grep -v original | head -1; }
wait_for() { # log pattern
  for _ in $(seq 1 120); do grep -q "$2" "$1" 2>/dev/null && return 0; sleep 0.5; done
  return 1
}
# say <transcript> <command...>: print the command the way a terminal shows it,
# run it, and keep both in the transcript the video's terminal frames read.
say() {
  local t=$1; shift
  { echo "\$ $*"; eval "$@"; echo; } 2>&1 | tee -a "$WORK/$t.log"
}
ROWS='docker compose exec -T postgres psql -U payments -d payments -tAc "select count(*) from payment"'
rows() { eval "$ROWS" | tr -d '[:space:]'; }
POST='curl -s -o /dev/null -w "%{http_code}" -H "Content-Type: application/json"'
# run_app <framework> <dir> <port>: start a built app and wait until it serves
run_app() {
  local j; j=$(jar "$2")
  if [ "$1" = spring ]; then "$JAVA" -jar "$j" --server.port="$3" >"$WORK/run-$1-$3.log" 2>&1 &
  else "$JAVA" -Dmicronaut.server.port="$3" -jar "$j" >"$WORK/run-$1-$3.log" 2>&1 & fi
  PIDS+=($!)
  wait_for "$WORK/run-$1-$3.log" "$( [ "$1" = spring ] && echo 'Started ' || echo 'Server Running')" || fail "$1 did not start"
}
stop_apps() { for p in "${PIDS[@]:-}"; do kill "$p" 2>/dev/null || true; wait "$p" 2>/dev/null || true; done; PIDS=(); }

echo "=== 0. Both apps, as written: all tests green ==="
(cd spring-payments && ./mvnw -q test)
(cd micronaut-payments && ./mvnw -q test)
echo "spring-payments and micronaut-payments: tests pass"

echo
echo "=== 1. @Transactional on a private method ==="
d=$(copy spring private-transactional)
(cd "$d" && ./mvnw test -Dtest=PrivateTransactionalTest >"$WORK/sp-private.log" 2>&1) \
  || fail "Spring: expected the build to pass and the rejected row to be written"
(cd "$d" && ./mvnw -q -DskipTests package)
run_app spring "$d" 8091
say term-sp-private "$POST -d '{\"amount\":90000,\"currency\":\"GBP\"}' localhost:8091/payments"
say term-sp-private "$ROWS"
[ "$(rows)" = 1 ] || fail "Spring private: expected the rejected payment to be written"
stop_apps
echo "Spring     builds, runs, rejected payment still written: 1 row"
d=$(copy micronaut private-transactional)
if (cd "$d" && ./mvnw -q -DskipTests compile >"$WORK/mn-private.log" 2>&1); then
  fail "Micronaut: expected the build to fail"
fi
grep -q "Method annotated as executable but is declared private" "$WORK/mn-private.log" \
  || fail "Micronaut: build failed for another reason"
echo "Micronaut  build fails: $(grep -o 'Method annotated as executable but is declared private[^.]*\.' "$WORK/mn-private.log" | head -1)"

echo
echo "=== 2. A self call to a @Transactional method, the apps as written ==="
(cd spring-payments && ./mvnw -q -DskipTests package)
(cd micronaut-payments && ./mvnw -q -DskipTests package)
for fw in spring micronaut; do
  port=$([ $fw = spring ] && echo 8091 || echo 8092)
  run_app $fw "$ROOT/$fw-payments" $port
  say term-$fw-self "$POST -d '{\"amount\":90000,\"currency\":\"GBP\"}' localhost:$port/payments"
  say term-$fw-self "$ROWS"
  [ "$(rows)" = 0 ] || fail "$fw: expected the call through the bean to roll back"
  say term-$fw-self "$POST -d '[{\"amount\":90000,\"currency\":\"GBP\"}]' localhost:$port/payments/batch"
  say term-$fw-self "$ROWS"
  want=$([ $fw = spring ] && echo 1 || echo 0)
  [ "$(rows)" = "$want" ] || fail "$fw: expected $want row(s) after the self call"
  stop_apps
done
echo "Spring     through the bean: rolled back. Through a self call: still written."
echo "Micronaut  through the bean: rolled back. Through a self call: rolled back."

echo "=== 3. findByCurrencyy ==="
d=$(copy spring finder-typo)
(cd "$d" && ./mvnw -DskipTests package >"$WORK/sp-typo-build.log" 2>&1) || fail "Spring: expected the build to pass"
if "$JAVA" -jar "$(jar "$d")" >"$WORK/sp-typo.log" 2>&1; then fail "Spring: expected startup to fail"; fi
grep -q "No property 'currencyy' found for type 'Payment'" "$WORK/sp-typo.log" || fail "Spring: failed for another reason"
echo "Spring     builds, then refuses to start: $(grep -o "No property 'currencyy' found for type 'Payment'; Did you mean 'currency'" "$WORK/sp-typo.log" | head -1)"
d=$(copy micronaut finder-typo)
if (cd "$d" && ./mvnw -q -DskipTests compile >"$WORK/mn-typo.log" 2>&1); then fail "Micronaut: expected the build to fail"; fi
grep -q "non-existent property: Currencyy" "$WORK/mn-typo.log" || fail "Micronaut: build failed for another reason"
echo "Micronaut  build fails: $(grep -o 'Cannot query entity \[Payment\] on non-existent property: Currencyy[^]]*\]' "$WORK/mn-typo.log" | head -1)"

echo
echo "=== 4. A missing Gateway bean ==="
d=$(copy spring missing-bean)
(cd "$d" && ./mvnw -q -DskipTests package)
if "$JAVA" -jar "$(jar "$d")" >"$WORK/sp-missing.log" 2>&1; then fail "Spring: expected startup to fail"; fi
grep -q "APPLICATION FAILED TO START" "$WORK/sp-missing.log" || fail "Spring: failed for another reason"
echo "Spring     APPLICATION FAILED TO START: $(grep -o "required a bean of type '[^']*' that could not be found" "$WORK/sp-missing.log" | head -1)"

d=$(copy micronaut missing-bean)
(cd "$d" && ./mvnw -q -DskipTests package)
"$JAVA" -Dmicronaut.server.port=8094 -jar "$(jar "$d")" >"$WORK/mn-missing.log" 2>&1 &
PIDS+=($!)
wait_for "$WORK/mn-missing.log" "Server Running" || fail "Micronaut: expected startup to succeed"
echo "Micronaut  $(grep -o 'Startup completed in [0-9]*ms. Server Running' "$WORK/mn-missing.log")"
say term-mn-missing "curl -s -o /dev/null -w '%{http_code}' -X POST 'localhost:8094/checkout?amount=2500'"
code=$(grep -E "^[0-9]{3}$" "$WORK/term-mn-missing.log" | tail -1)
[ "$code" = 500 ] || fail "Micronaut: expected 500 on the first request, got $code"
grep -q "No bean of type \[dev.codewithsam.payments.Gateway\] exists" "$WORK/mn-missing.log" || fail "Micronaut: 500 for another reason"
echo "Micronaut  POST /checkout -> $code, No bean of type [Gateway] exists"

d=$(copy micronaut missing-bean eager-init)
(cd "$d" && ./mvnw -q -DskipTests package)
"$JAVA" -Dmicronaut.server.port=8095 -jar "$(jar "$d")" >"$WORK/mn-eager.log" 2>&1 &
PIDS+=($!)
wait_for "$WORK/mn-eager.log" "Error starting Micronaut server" || fail "Micronaut eager: expected startup to fail"
grep -q "Server Running" "$WORK/mn-eager.log" && fail "Micronaut eager: server should not be running"
echo "Micronaut  with eagerInitSingletons(true): Error starting Micronaut server"

echo
echo "ALL CLAIMS HOLD"
