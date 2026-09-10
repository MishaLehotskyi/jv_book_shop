package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateCartItemRequestDto(
        @NotNull(message = "Book id must not be null")
        @Positive(message = "Book id must be positive")
        Long bookId,

        @Positive(message = "Quantity must be positive")
        int quantity
) {
}
