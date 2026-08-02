package com.example.demo.controller;

import com.example.demo.dto.CreateCartItemRequestDto;
import com.example.demo.dto.ShoppingCartDto;
import com.example.demo.dto.UpdateCartItemRequestDto;
import com.example.demo.model.User;
import com.example.demo.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart management", description = "Endpoints for managing the shopping cart")
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @GetMapping
    @Operation(summary = "Get the shopping cart",
            description = "Get the shopping cart of the currently authenticated user")
    public ShoppingCartDto getShoppingCart(Authentication authentication) {
        return shoppingCartService.getByUserId(getUserId(authentication));
    }

    @PostMapping
    @Operation(summary = "Add a book to the shopping cart",
            description = "Add a book to the shopping cart of the currently authenticated user. "
                    + "If the book is already in the cart, its quantity is increased")
    public ShoppingCartDto addBookToCart(Authentication authentication,
                                         @RequestBody @Valid CreateCartItemRequestDto requestDto) {
        return shoppingCartService.addBookToCart(getUserId(authentication), requestDto);
    }

    @PutMapping("/items/{cartItemId}")
    @Operation(summary = "Update a cart item quantity",
            description = "Update the quantity of a book in the shopping cart")
    public ShoppingCartDto updateCartItem(Authentication authentication,
                                          @PathVariable Long cartItemId,
                                          @RequestBody @Valid UpdateCartItemRequestDto requestDto) {
        return shoppingCartService.updateCartItemQuantity(
                getUserId(authentication), cartItemId, requestDto);
    }

    @DeleteMapping("/items/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove a book from the shopping cart",
            description = "Remove a cart item from the shopping cart by its id")
    public void removeCartItem(Authentication authentication, @PathVariable Long cartItemId) {
        shoppingCartService.removeCartItem(getUserId(authentication), cartItemId);
    }

    private Long getUserId(Authentication authentication) {
        return ((User) authentication.getPrincipal()).getId();
    }
}
