package com.digitalassetvault.service;

import com.digitalassetvault.domain.Wallet;
import com.digitalassetvault.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletValidator walletValidator;

    @InjectMocks
    private WalletService walletService;

    @Test
    void shouldCreateWallet() {
        String address = "0x1234567890abcdef1234567890abcdef12345678";
        String blockchain = "ethereum";
        String keyReference = "hsm-key-dev-001";

        when(walletRepository.save(any(Wallet.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Wallet result = walletService.create(
                address,
                blockchain,
                keyReference);

        assertNotNull(result);
        assertNotNull(result.getId());

        assertEquals(address, result.getAddress());
        assertEquals(blockchain, result.getBlockchain());
        assertEquals(keyReference, result.getKeyReference());
        assertEquals("ACTIVE", result.getStatus());

        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        verify(walletValidator).validate(
                address,
                blockchain,
                keyReference);

        verify(walletRepository).save(any(Wallet.class));
    }

    @Test
    void shouldRejectInvalidWallet() {
        String address = "invalid";
        String blockchain = "ethereum";
        String keyReference = "hsm-key-dev-001";

        doThrow(new IllegalArgumentException("Invalid Ethereum address"))
                .when(walletValidator)
                .validate(address, blockchain, keyReference);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> walletService.create(
                        address,
                        blockchain,
                        keyReference));

        assertEquals(
                "Invalid Ethereum address",
                exception.getMessage());

        verify(walletValidator).validate(
                address,
                blockchain,
                keyReference);

        verify(walletRepository, never()).save(any(Wallet.class));
    }
}