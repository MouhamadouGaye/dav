
package com.digitalassetvault.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WalletCreateRequest(

        @NotBlank @Size(max = 255) String address,

        @NotBlank @Size(max = 50) String blockchain,

        @NotBlank @Size(max = 255) String keyReference) {
}