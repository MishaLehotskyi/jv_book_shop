package com.example.demo.controller;

import com.example.demo.dto.CreateOrderRequestDto;
import com.example.demo.dto.OrderDto;
import com.example.demo.dto.OrderItemDto;
import com.example.demo.dto.UpdateOrderStatusRequestDto;
import com.example.demo.model.User;
import com.example.demo.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order management", description = "Endpoints for managing orders")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Place an order",
            description = "Place an order with all the books from the shopping cart "
                    + "of the currently authenticated user and clear the cart")
    public OrderDto placeOrder(Authentication authentication,
                               @RequestBody @Valid CreateOrderRequestDto requestDto) {
        return orderService.placeOrder(getUserId(authentication), requestDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get the order history",
            description = "Get a page of the orders of the currently authenticated user "
                    + "with pagination and sorting")
    public Page<OrderDto> getOrderHistory(Authentication authentication,
                                          @ParameterObject Pageable pageable) {
        return orderService.getOrderHistory(getUserId(authentication), pageable);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an order status",
            description = "Update the status of an existing order by its id")
    public OrderDto updateOrderStatus(@PathVariable Long id,
                                      @RequestBody @Valid UpdateOrderStatusRequestDto requestDto) {
        return orderService.updateStatus(id, requestDto);
    }

    @GetMapping("/{orderId}/items")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get all items of an order",
            description = "Get a page of the items of the given order "
                    + "with pagination and sorting")
    public Page<OrderItemDto> getOrderItems(Authentication authentication,
                                            @PathVariable Long orderId,
                                            @ParameterObject Pageable pageable) {
        return orderService.getOrderItems(getUserId(authentication), orderId, pageable);
    }

    @GetMapping("/{orderId}/items/{itemId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get an order item",
            description = "Get a single item of the given order by its id")
    public OrderItemDto getOrderItem(Authentication authentication,
                                     @PathVariable Long orderId,
                                     @PathVariable Long itemId) {
        return orderService.getOrderItem(getUserId(authentication), orderId, itemId);
    }

    private Long getUserId(Authentication authentication) {
        return ((User) authentication.getPrincipal()).getId();
    }
}
