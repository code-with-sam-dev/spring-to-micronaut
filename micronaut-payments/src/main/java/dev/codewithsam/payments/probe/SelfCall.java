package dev.codewithsam.payments.probe;

import jakarta.inject.Singleton;

@Singleton
public class SelfCall {
    public void outer() { inner(); }
    @Counted
    public void inner() { }
}
