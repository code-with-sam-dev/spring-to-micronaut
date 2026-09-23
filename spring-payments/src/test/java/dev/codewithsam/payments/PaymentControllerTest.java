package dev.codewithsam.payments;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class PaymentControllerTest {

    @LocalServerPort int port;

    RestClient client() {
        return RestClient.builder().baseUrl("http://localhost:" + port).build();
    }

    @Test
    void postReturnsCreatedWithALocation() {
        var response = client().post().uri("/payments")
                .header("Content-Type", "application/json")
                .body("{\"amount\":2500,\"currency\":\"GBP\"}")
                .retrieve().toBodilessEntity();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/payments/");
    }

    @Test
    void getReturnsNotFoundWhenThereIsNothingThere() {
        var status = client().get().uri("/payments/999999")
                .exchange((request, response) -> response.getStatusCode());
        assertThat(status).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
