package com.example.demo.dto;

import com.example.demo.model.Status;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequestDto(
        @NotNull(message = "Status must not be null")
        Status status
) {
}
