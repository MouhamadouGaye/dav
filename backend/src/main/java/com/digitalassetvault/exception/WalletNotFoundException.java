package com.digitalassetvault.exception;

import java.lang.RuntimeException;

import java.util.UUID;

public class WalletNotFoundException extends RuntimeException {

    public WalletNotFoundException(UUID id) {
        super("Wallet not found: " + id);
    }
}