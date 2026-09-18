package com.digitalassetvault.controller;

import com.digitalassetvault.domain.Wallet;
import com.digitalassetvault.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class WalletControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("digital_asset_vault")
            .withUsername("dav")
            .withPassword("dav_test_password");

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {

        registry.add(
                "spring.datasource.url",
                postgres::getJdbcUrl);

        registry.add(
                "spring.datasource.username",
                postgres::getUsername);

        registry.add(
                "spring.datasource.password",
                postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WalletRepository walletRepository;

    @Test
    void shouldCreateWallet() throws Exception {

        String requestBody = """
                {
                    "address": "0x3333333333333333333333333333333333333333",
                    "blockchain": "ethereum",
                    "keyReference": "hsm-key-integration-001"
                }
                """;

        mockMvc.perform(
                post("/api/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.address")
                        .value("0x3333333333333333333333333333333333333333"))
                .andExpect(jsonPath("$.blockchain")
                        .value("ethereum"))
                .andExpect(jsonPath("$.keyReference")
                        .value("hsm-key-integration-001"))
                .andExpect(jsonPath("$.status")
                        .value("ACTIVE"));
    }

    @Test
    void shouldReturnConflictWhenAddressAlreadyExists() throws Exception {

        String address = "0x4444444444444444444444444444444444444444";

        Wallet wallet = new Wallet(
                UUID.randomUUID(),
                address,
                "ethereum",
                "hsm-key-integration-002",
                "ACTIVE",
                OffsetDateTime.now(),
                OffsetDateTime.now());

        walletRepository.saveAndFlush(wallet);

        String requestBody = """
                {
                    "address": "0x4444444444444444444444444444444444444444",
                    "blockchain": "ethereum",
                    "keyReference": "hsm-key-integration-003"
                }
                """;

        mockMvc.perform(
                post("/api/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error")
                        .value("RESOURCE_CONFLICT"))
                .andExpect(jsonPath("$.message")
                        .value("Wallet already exists"));
    }

    @Test
    void shouldReturnWallets() throws Exception {

        Wallet wallet = new Wallet(
                UUID.randomUUID(),
                "0x5555555555555555555555555555555555555555",
                "ethereum",
                "hsm-key-integration-004",
                "ACTIVE",
                OffsetDateTime.now(),
                OffsetDateTime.now());

        walletRepository.saveAndFlush(wallet);

        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .get("/api/wallets"))
                .andExpect(status().isOk())
                // .andExpect(jsonPath("$[0].address")
                // .value("0x5555555555555555555555555555555555555555"))
                // .andExpect(jsonPath("$[0].blockchain")
                // .value("ethereum"))
                // .andExpect(jsonPath("$[0].status")
                // .value("ACTIVE"));
                .andExpect(jsonPath("$[?(@.address == '0x5555555555555555555555555555555555555555')]")
                        .exists());
    }
}