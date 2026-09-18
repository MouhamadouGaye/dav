package com.digitalassetvault.repository;

import com.digitalassetvault.domain.Wallet;

import com.digitalassetvault.repository.WalletRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
class WalletRepositoryIntegrationTest {

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
    private WalletRepository walletRepository;

    @Test
    void shouldSaveAndFindWallet() {

        Wallet wallet = new Wallet(
                UUID.randomUUID(),
                "0x1111111111111111111111111111111111111111",
                "ethereum",
                "hsm-key-test-001",
                "ACTIVE",
                OffsetDateTime.now(),
                OffsetDateTime.now());

        Wallet saved = walletRepository.save(wallet);

        assertThat(saved.getId()).isNotNull();

        assertThat(walletRepository.findById(saved.getId()))
                .isPresent()
                .get()
                .extracting(Wallet::getAddress)
                .isEqualTo(
                        "0x1111111111111111111111111111111111111111");
    }

    @Test
    void shouldRejectDuplicateAddress() {

        String address = "0x2222222222222222222222222222222222222222";

        Wallet firstWallet = new Wallet(
                UUID.randomUUID(),
                address,
                "ethereum",
                "hsm-key-test-002",
                "ACTIVE",
                OffsetDateTime.now(),
                OffsetDateTime.now());

        Wallet secondWallet = new Wallet(
                UUID.randomUUID(),
                address,
                "ethereum",
                "hsm-key-test-003",
                "ACTIVE",
                OffsetDateTime.now(),
                OffsetDateTime.now());

        walletRepository.saveAndFlush(firstWallet);

        org.junit.jupiter.api.Assertions.assertThrows(
                org.springframework.dao.DataIntegrityViolationException.class,
                () -> walletRepository.saveAndFlush(secondWallet));
    }
}