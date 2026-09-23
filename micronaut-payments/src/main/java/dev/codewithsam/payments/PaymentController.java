package dev.codewithsam.payments;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import java.net.URI;

@Controller("/payments")
public class PaymentController {

    private final PaymentService service;
    private final PaymentRepository repository;

    public PaymentController(PaymentService service, PaymentRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @Post
    public HttpResponse<Void> create(@Body NewPayment body) {
        Long id = service.record(body.amount(), body.currency());
        return HttpResponse.created(URI.create("/payments/" + id));
    }

    @Get("/{id}")
    public HttpResponse<PaymentView> get(@PathVariable Long id) {
        return repository.findById(id)
                .map(PaymentView::of)
                .map(HttpResponse::ok)
                .orElse(HttpResponse.notFound());
    }
}
