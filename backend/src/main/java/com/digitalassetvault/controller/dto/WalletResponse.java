package com.digitalassetvault.controller.dto;

import com.digitalassetvault.domain.Wallet;

import java.time.OffsetDateTime;
import java.util.UUID;

public record WalletResponse(
        UUID id,
        String address,
        String blockchain,
        String keyReference,
        String status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {

    public static WalletResponse from(Wallet wallet) {
        return new WalletResponse(
                wallet.getId(),
                wallet.getAddress(),
                wallet.getBlockchain(),
                wallet.getKeyReference(),
                wallet.getStatus(),
                wallet.getCreatedAt(),
                wallet.getUpdatedAt());
    }
}