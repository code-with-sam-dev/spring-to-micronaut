package dev.codewithsam.payments;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PrivateTransactionalTest {

    @Autowired PaymentService service;
    @Autowired PaymentRepository repository;

    @BeforeEach
    void empty() {
        repository.deleteAll();
    }

    @Test
    void theRejectedPaymentIsStillWritten() {
        assertThatThrownBy(() -> service.record(90_000, "GBP"))
                .isInstanceOf(PaymentRejected.class);
        assertThat(repository.count()).isEqualTo(1);
    }
}
