package dev.codewithsam.payments;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CheckoutController {

    private final Checkout checkout;

    public CheckoutController(Checkout checkout) {
        this.checkout = checkout;
    }

    @PostMapping("/checkout")
    public String pay(@RequestParam long amount) {
        return checkout.pay(amount);
    }
}
