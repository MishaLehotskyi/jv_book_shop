package com.example.demo.service;

import com.example.demo.dto.CreateOrderRequestDto;
import com.example.demo.dto.OrderDto;
import com.example.demo.dto.OrderItemDto;
import com.example.demo.dto.UpdateOrderStatusRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDto placeOrder(Long userId, CreateOrderRequestDto requestDto);

    Page<OrderDto> getOrderHistory(Long userId, Pageable pageable);

    OrderDto updateStatus(Long orderId, UpdateOrderStatusRequestDto requestDto);

    Page<OrderItemDto> getOrderItems(Long userId, Long orderId, Pageable pageable);

    OrderItemDto getOrderItem(Long userId, Long orderId, Long itemId);
}
