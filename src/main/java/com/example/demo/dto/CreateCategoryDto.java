package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCategoryDto(
        @NotBlank(message = "Name must not be blank")
        @Size(max = 255, message = "Name must be at most 255 characters")
        String name,

        @Size(max = 255, message = "Description must be at most 255 characters")
        String description
) {
}
