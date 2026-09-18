package com.digitalassetvault.controller;

import com.digitalassetvault.controller.dto.WalletCreateRequest;
import com.digitalassetvault.controller.dto.WalletResponse;
import com.digitalassetvault.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping
    public List<WalletResponse> findAll() {
        return walletService.findAll()
                .stream()
                .map(WalletResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public WalletResponse findById(@PathVariable UUID id) {
        return WalletResponse.from(
                walletService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WalletResponse create(
            @Valid @RequestBody WalletCreateRequest request) {

        return WalletResponse.from(
                walletService.create(
                        request.address(),
                        request.blockchain(),
                        request.keyReference()));
    }
}