package dev.codewithsam.payments;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record PaymentView(Long id, long amount, String currency) {

    static PaymentView of(Payment p) {
        return new PaymentView(p.getId(), p.getAmount(), p.getCurrency());
    }
}
