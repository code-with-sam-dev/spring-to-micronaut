package dev.codewithsam.payments;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@MicronautTest
class PaymentControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    void postReturnsCreatedWithALocation() {
        var response = client.toBlocking().exchange(
                HttpRequest.POST("/payments", new NewPayment(2500, "GBP")));
        assertThat(response.code()).isEqualTo(HttpStatus.CREATED.getCode());
        assertThat(response.header("Location")).startsWith("/payments/");
    }

    @Test
    void getReturnsNotFoundWhenThereIsNothingThere() {
        assertThatThrownBy(() -> client.toBlocking().exchange("/payments/999999"))
                .isInstanceOfSatisfying(HttpClientResponseException.class,
                        e -> assertThat(e.code()).isEqualTo(HttpStatus.NOT_FOUND.getCode()));
    }
}
