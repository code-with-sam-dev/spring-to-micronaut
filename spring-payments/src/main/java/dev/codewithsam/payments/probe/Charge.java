package dev.codewithsam.payments.probe;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Charge {
    @Id @GeneratedValue Long id;
    long amount;
    String currency;
}
