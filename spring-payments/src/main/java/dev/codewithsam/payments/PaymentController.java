package dev.codewithsam.payments;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService service;
    private final PaymentRepository repository;

    public PaymentController(PaymentService service, PaymentRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody NewPayment body) {
        Long id = service.record(body.amount(), body.currency());
        return ResponseEntity.created(URI.create("/payments/" + id)).build();
    }

    @PostMapping("/batch")
    public List<Long> createAll(@RequestBody List<NewPayment> body) {
        return service.recordAll(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentView> get(@PathVariable Long id) {
        return repository.findById(id)
                .map(PaymentView::of)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
