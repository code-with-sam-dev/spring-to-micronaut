package dev.codewithsam.payments;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Payment {

    @Id
    @GeneratedValue
    private Long id;
    private long amount;
    private String currency;

    protected Payment() {
    }

    public Payment(long amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public Long getId() { return id; }
    public long getAmount() { return amount; }
    public String getCurrency() { return currency; }
}
