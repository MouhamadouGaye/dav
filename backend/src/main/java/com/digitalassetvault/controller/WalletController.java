package com.digitalassetvault.controller;

import com.digitalassetvault.domain.Wallet;
import com.digitalassetvault.service.WalletService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping
    public List<Wallet> findAll() {
        return walletService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Wallet create(@RequestBody CreateWalletRequest request) {
        return walletService.create(
                request.address(),
                request.blockchain(),
                request.keyReference());
    }

    public record CreateWalletRequest(
            String address,
            String blockchain,
            String keyReference) {
    }
}