package dev.codewithsam.payments.probe;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class TxProbe {

    public boolean outerCallsPrivate() { return privateTx(); }

    @Transactional
    private boolean privateTx() { return TransactionSynchronizationManager.isActualTransactionActive(); }

    public boolean outerCallsPublic() { return publicTx(); }

    @Transactional
    public boolean publicTx() { return TransactionSynchronizationManager.isActualTransactionActive(); }
}
