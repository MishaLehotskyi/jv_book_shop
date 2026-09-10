package com.example.demo.dto;

public record OrderItemDto(
        Long id,
        Long bookId,
        int quantity
) {
}
