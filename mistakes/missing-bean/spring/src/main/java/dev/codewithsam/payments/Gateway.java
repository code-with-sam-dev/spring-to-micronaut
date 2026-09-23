package dev.codewithsam.payments;

public interface Gateway {

    String charge(long amount);
}
