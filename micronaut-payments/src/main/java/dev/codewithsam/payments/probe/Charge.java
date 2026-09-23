package dev.codewithsam.payments.probe;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

@MappedEntity
public record Charge(@Id @GeneratedValue Long id, long amount, String currency) {
}
