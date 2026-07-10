package com.example.demo.mapper;

import com.example.demo.dto.UserRegistrationRequestDto;
import com.example.demo.dto.UserResponseDto;
import com.example.demo.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDto toDto(User user);

    User toModel(UserRegistrationRequestDto requestDto);
}
