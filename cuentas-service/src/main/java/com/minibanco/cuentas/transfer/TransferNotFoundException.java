package com.minibanco.cuentas.transfer;

public class TransferNotFoundException extends RuntimeException {
    public TransferNotFoundException(Long transferId) {
        super("Transfer not found with id: " + transferId);
    }
}
