package dev.codewithsam.payments.probe;

import io.micronaut.transaction.TransactionOperations;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import java.sql.Connection;

@Singleton
public class TxProbe {
    private final TransactionOperations<Connection> tx;
    public TxProbe(TransactionOperations<Connection> tx) { this.tx = tx; }

    public boolean outerCallsPublic() { return publicTx(); }

    @Transactional
    public boolean publicTx() { return tx.findTransactionStatus().isPresent(); }
}
