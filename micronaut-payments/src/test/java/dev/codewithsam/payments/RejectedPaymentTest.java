package dev.codewithsam.payments;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The same service as spring-payments. Micronaut's generated subclass
 * overrides record(), so the call from recordAll() goes through it too.
 */
@MicronautTest(startApplication = false, transactional = false)
class RejectedPaymentTest {

    @Inject PaymentService service;
    @Inject PaymentRepository repository;

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
    void throughASelfCallTheRejectedPaymentIsAlsoRolledBack() {
        assertThatThrownBy(() -> service.recordAll(List.of(new NewPayment(90_000, "GBP"))))
                .isInstanceOf(PaymentRejected.class);
        assertThat(repository.count()).isZero();
    }
}
