package dev.codewithsam.payments;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

@MappedEntity
public class Payment {

    @Id
    @GeneratedValue
    private Long id;
    private final long amount;
    private final String currency;

    public Payment(long amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public long getAmount() { return amount; }
    public String getCurrency() { return currency; }
}
