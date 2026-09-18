package com.digitalassetvault.service;

import com.digitalassetvault.domain.Wallet;
import com.digitalassetvault.repository.WalletRepository;
import com.digitalassetvault.exception.WalletNotFoundException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletValidator walletValidator;

    public WalletService(
            WalletRepository walletRepository,
            WalletValidator walletValidator) {

        this.walletRepository = walletRepository;
        this.walletValidator = walletValidator;
    }

    public List<Wallet> findAll() {
        return walletRepository.findAll();
    }

    public Wallet findById(UUID id) {
        return walletRepository.findById(id)
                .orElseThrow(() -> new WalletNotFoundException(id));
    }

    public Wallet create(
            String address,
            String blockchain,
            String keyReference) {

        walletValidator.validate(
                address,
                blockchain,
                keyReference);

        OffsetDateTime now = OffsetDateTime.now();

        Wallet wallet = new Wallet(
                UUID.randomUUID(),
                address,
                blockchain,
                keyReference,
                "ACTIVE",
                now,
                now);

        return walletRepository.save(wallet);
    }
}