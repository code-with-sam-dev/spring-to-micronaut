package dev.codewithsam.payments;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;

@Controller
public class CheckoutController {

    private final Checkout checkout;

    public CheckoutController(Checkout checkout) {
        this.checkout = checkout;
    }

    @Post("/checkout")
    public String pay(@QueryValue long amount) {
        return checkout.pay(amount);
    }
}
