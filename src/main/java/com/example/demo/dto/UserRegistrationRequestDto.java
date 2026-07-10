package com.example.demo.dto;

import com.example.demo.validation.FieldMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@FieldMatch(
        field = "password",
        fieldMatch = "repeatPassword",
        message = "Password and repeatPassword must match"
)
public record UserRegistrationRequestDto(
        @NotBlank
        @Email
        String email,
        @NotBlank
        @Size(min = 8, max = 100)
        String password,
        @NotBlank
        @Size(min = 8, max = 100)
        String repeatPassword,
        @NotBlank
        String firstName,
        @NotBlank
        String lastName,
        String shippingAddress
) {
}
