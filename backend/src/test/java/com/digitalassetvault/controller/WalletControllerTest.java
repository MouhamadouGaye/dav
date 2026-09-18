package com.digitalassetvault.controller;

import com.digitalassetvault.domain.Wallet;
import com.digitalassetvault.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WalletController.class)
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    @Test
    void shouldCreateWallet() throws Exception {

        Wallet wallet = new Wallet(
                UUID.randomUUID(),
                "0x1234567890abcdef1234567890abcdef12345678",
                "ethereum",
                "hsm-key-dev-001",
                "ACTIVE",
                OffsetDateTime.now(),
                OffsetDateTime.now());

        when(walletService.create(
                anyString(),
                anyString(),
                anyString())).thenReturn(wallet);

        mockMvc.perform(
                post("/api/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "address": "0x1234567890abcdef1234567890abcdef12345678",
                                  "blockchain": "ethereum",
                                  "keyReference": "hsm-key-dev-001"
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldRejectEmptyFields() throws Exception {

        mockMvc.perform(
                post("/api/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "address": "",
                                  "blockchain": "",
                                  "keyReference": ""
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectUnsupportedBlockchain() throws Exception {

        when(walletService.create(
                anyString(),
                anyString(),
                anyString())).thenThrow(
                        new IllegalArgumentException("Unsupported blockchain: solana"));

        mockMvc.perform(
                post("/api/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "address": "0x1234567890abcdef1234567890abcdef12345678",
                                  "blockchain": "solana",
                                  "keyReference": "hsm-key-dev-001"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectInvalidAddress() throws Exception {

        when(walletService.create(
                anyString(),
                anyString(),
                anyString())).thenThrow(
                        new IllegalArgumentException("Invalid Ethereum address"));

        mockMvc.perform(
                post("/api/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "address": "bonjour",
                                  "blockchain": "ethereum",
                                  "keyReference": "hsm-key-dev-001"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectInvalidKeyReference() throws Exception {

        when(walletService.create(
                anyString(),
                anyString(),
                anyString())).thenThrow(
                        new IllegalArgumentException("Invalid HSM key reference"));

        mockMvc.perform(
                post("/api/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "address": "0x1234567890abcdef1234567890abcdef12345678",
                                  "blockchain": "ethereum",
                                  "keyReference": "private-key-123"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}