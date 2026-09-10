package com.example.demo.dto;

import jakarta.validation.constraints.Positive;

public record UpdateCartItemRequestDto(
        @Positive(message = "Quantity must be positive")
        int quantity
) {
}
