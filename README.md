# Spring Boot to Micronaut

The same payments service written twice, once in Spring Boot 4.1.1 and once in
Micronaut 5.1.5, both on Java 25 and Postgres 17. It goes with the Code with Sam
video on what Micronaut actually does at compile time.

Most of the code transfers almost line for line. Four small mistakes do not
behave the same way, and `mistakes/` holds each one so you can try it yourself.

| Mistake | Spring Boot | Micronaut |
|---|---|---|
| `@Transactional` on a private method | builds and runs, the rejected payment is still written | the build fails |
| a `@Transactional` method called from the same class | no transaction, the rejected payment is still written | the call is intercepted, rolled back |
| `findByCurrencyy` in a repository | builds, then refuses to start | the build fails |
| a bean nobody provides | refuses to start | starts, then the first request fails with a 500 |

`eagerInitSingletons(true)` makes Micronaut refuse to start on the missing
bean too. It does not fix the mistake, it moves when you find out about it.
Providing the bean fixes it.

These are the default modes: Spring's proxy based transactions, without AOT,
and Micronaut's lazy singletons. Spring's AspectJ mode does apply transactions
to calls from inside the class.

## Run it

```bash
docker compose up -d
scripts/verify.sh
```

`verify.sh` runs both test suites, then lays each mistake over a scratch copy of
the app and checks every result in the table above. It fails loudly if any of
them stops being true.

Each app on its own:

```bash
cd spring-payments && ./mvnw spring-boot:run      # port 8091
cd micronaut-payments && ./mvnw mn:run            # port 8092
```
