package dev.codewithsam.payments;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record NewPayment(long amount, String currency) {
}
