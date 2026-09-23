package dev.codewithsam.payments;

import org.springframework.stereotype.Service;

@Service
public class Checkout {

    private final Gateway gateway;

    public Checkout(Gateway gateway) {
        this.gateway = gateway;
    }

    public String pay(long amount) {
        return gateway.charge(amount);
    }
}
