package dev.codewithsam.payments;

import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import java.util.List;

@Singleton
public class PaymentService {

    static final long LIMIT = 50_000;

    private final PaymentRepository repository;

    public PaymentService(PaymentRepository repository) {
        this.repository = repository;
    }

    public List<Long> recordAll(List<NewPayment> payments) {
        return payments.stream()
                .map(p -> record(p.amount(), p.currency()))
                .toList();
    }

    @Transactional
    public Long record(long amount, String currency) {
        Payment payment = repository.save(new Payment(amount, currency));
        if (amount > LIMIT) {
            throw new PaymentRejected(amount);
        }
        return payment.getId();
    }
}
