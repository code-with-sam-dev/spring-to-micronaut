package dev.codewithsam.payments.probe;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChargeRepository extends JpaRepository<Charge, Long> {
    List<Charge> findByCurrencyy(String currency);
}
