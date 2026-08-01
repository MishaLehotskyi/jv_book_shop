package com.example.demo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

public record CreateBookRequestDto(
        @NotBlank(message = "Title must not be blank")
        @Size(max = 255, message = "Title must be at most 255 characters")
        String title,

        @NotBlank(message = "Author must not be blank")
        @Size(max = 255, message = "Author must be at most 255 characters")
        String author,

        @NotBlank(message = "ISBN must not be blank")
        @Pattern(
                regexp = "^(?:\\d{9}[\\dXx]|\\d{13})$",
                message = "ISBN must be a valid 10 or 13 digit number"
        )
        String isbn,

        @NotNull(message = "Price must not be null")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        @Digits(
                integer = 10,
                fraction = 2,
                message = "Price must have at most 10 digits and 2 decimals"
        )
        BigDecimal price,

        @Size(max = 2000, message = "Description must be at most 2000 characters")
        String description,

        @Size(max = 512, message = "Cover image URL must be at most 512 characters")
        String coverImage,

        @NotEmpty(message = "Category ids must not be empty")
        List<@Positive(message = "Category id must be positive") Long> categoryIds
) {
}
