package com.example.demo.service;

import com.example.demo.dto.CreateCartItemRequestDto;
import com.example.demo.dto.ShoppingCartDto;
import com.example.demo.dto.UpdateCartItemRequestDto;
import com.example.demo.model.User;

public interface ShoppingCartService {
    void createShoppingCart(User user);

    ShoppingCartDto getByUserId(Long userId);

    ShoppingCartDto addBookToCart(Long userId, CreateCartItemRequestDto requestDto);

    ShoppingCartDto updateCartItemQuantity(Long userId, Long cartItemId,
                                           UpdateCartItemRequestDto requestDto);

    void removeCartItem(Long userId, Long cartItemId);
}
