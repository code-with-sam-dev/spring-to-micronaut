package dev.codewithsam.payments.probe;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@MicronautTest(startApplication = false)
class TxProbeTest {
    @Inject TxProbe probe;
    @Test
    void probe() {
        System.out.println("PROBE public called directly, tx active: " + probe.publicTx());
        System.out.println("PROBE public via self call, tx active: " + probe.outerCallsPublic());
        System.out.println("PROBE class: " + probe.getClass().getName());
    }
}
