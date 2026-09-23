package dev.codewithsam.payments;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * record() saves the payment, then checks the limit. Only a transaction can
 * take the insert back when the check rejects it.
 */
@SpringBootTest
class RejectedPaymentTest {

    @Autowired PaymentService service;
    @Autowired PaymentRepository repository;

    @BeforeEach
    void empty() {
        repository.deleteAll();
    }

    @Test
    void throughTheBeanTheRejectedPaymentIsRolledBack() {
        assertThatThrownBy(() -> service.record(90_000, "GBP"))
                .isInstanceOf(PaymentRejected.class);
        assertThat(repository.count()).isZero();
    }

    @Test
    void throughASelfCallTheRejectedPaymentIsStillWritten() {
        assertThatThrownBy(() -> service.recordAll(List.of(new NewPayment(90_000, "GBP"))))
                .isInstanceOf(PaymentRejected.class);
        assertThat(repository.count()).isEqualTo(1);
    }
}
