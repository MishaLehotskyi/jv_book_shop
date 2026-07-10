package com.example.demo.service;

import com.example.demo.dto.UserRegistrationRequestDto;
import com.example.demo.dto.UserResponseDto;
import com.example.demo.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto request) throws RegistrationException;
}
