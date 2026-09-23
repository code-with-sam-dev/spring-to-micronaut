package dev.codewithsam.payments.probe;

import org.springframework.stereotype.Service;

@Service
public class Checkout {
    private final Gateway gateway;
    public Checkout(Gateway gateway) { this.gateway = gateway; }
    public String pay() { return gateway.charge(); }
}
