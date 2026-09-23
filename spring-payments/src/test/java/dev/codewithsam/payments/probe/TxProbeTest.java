package dev.codewithsam.payments.probe;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TxProbeTest {
    @Autowired TxProbe probe;
    @Test
    void probe() {
        System.out.println("PROBE private via self call, tx active: " + probe.outerCallsPrivate());
        System.out.println("PROBE public called directly, tx active: " + probe.publicTx());
        System.out.println("PROBE public via self call, tx active: " + probe.outerCallsPublic());
        System.out.println("PROBE class: " + probe.getClass().getName());
    }
}
