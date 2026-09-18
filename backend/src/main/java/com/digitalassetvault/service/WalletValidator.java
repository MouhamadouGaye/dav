package com.digitalassetvault.service;

import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class WalletValidator {

    private static final Set<String> SUPPORTED_BLOCKCHAINS = Set.of(
            "ethereum");

    private static final String ETHEREUM_ADDRESS_REGEX = "^0x[0-9a-fA-F]{40}$";

    private static final String KEY_REFERENCE_REGEX = "^hsm-key-[a-zA-Z0-9-]+$";

    public void validate(
            String address,
            String blockchain,
            String keyReference) {

        validateBlockchain(blockchain);
        validateAddress(address, blockchain);
        validateKeyReference(keyReference);
    }

    private void validateBlockchain(String blockchain) {
        if (!SUPPORTED_BLOCKCHAINS.contains(blockchain.toLowerCase())) {
            throw new IllegalArgumentException(
                    "Unsupported blockchain: " + blockchain);
        }
    }

    private void validateAddress(
            String address,
            String blockchain) {

        if ("ethereum".equalsIgnoreCase(blockchain)
                && !address.matches(ETHEREUM_ADDRESS_REGEX)) {

            throw new IllegalArgumentException(
                    "Invalid Ethereum address");
        }
    }

    private void validateKeyReference(String keyReference) {
        if (!keyReference.matches(KEY_REFERENCE_REGEX)) {
            throw new IllegalArgumentException(
                    "Invalid HSM key reference");
        }
    }
}