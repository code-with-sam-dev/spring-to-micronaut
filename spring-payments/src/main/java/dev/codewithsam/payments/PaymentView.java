package dev.codewithsam.payments;

public record PaymentView(Long id, long amount, String currency) {

    static PaymentView of(Payment p) {
        return new PaymentView(p.getId(), p.getAmount(), p.getCurrency());
    }
}
