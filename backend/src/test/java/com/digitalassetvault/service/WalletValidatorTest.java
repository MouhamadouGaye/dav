
package com.digitalassetvault.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WalletValidatorTest {

    private final WalletValidator validator = new WalletValidator();

    @Test
    void shouldAcceptValidEthereumWallet() {
        assertDoesNotThrow(() -> validator.validate(
                "0x1234567890abcdef1234567890abcdef12345678",
                "ethereum",
                "hsm-key-dev-001"));
    }

    @Test
    void shouldRejectUnsupportedBlockchain() {
        assertThrows(
                IllegalArgumentException.class,
                () -> validator.validate(
                        "0x1234567890abcdef1234567890abcdef12345678",
                        "solana",
                        "hsm-key-dev-001"));
    }

    @Test
    void shouldRejectInvalidEthereumAddress() {
        assertThrows(
                IllegalArgumentException.class,
                () -> validator.validate(
                        "bonjour",
                        "ethereum",
                        "hsm-key-dev-001"));
    }

    @Test
    void shouldRejectInvalidKeyReference() {
        assertThrows(
                IllegalArgumentException.class,
                () -> validator.validate(
                        "0x1234567890abcdef1234567890abcdef12345678",
                        "ethereum",
                        "private-key-123"));
    }
}