package dev.codewithsam.payments;

public class PaymentRejected extends RuntimeException {

    public PaymentRejected(long amount) {
        super("Payment of " + amount + " is over the limit");
    }
}
