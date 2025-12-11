package com.ecommerce.controller;

import com.ecommerce.dto.OrderDTO;
import com.ecommerce.service.OrderService;
import com.ecommerce.security.CustomUserDetails;
//import com.ecommerce.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping

    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderDTO orderDTO,
            Authentication authentication) {
        Long userId = extractUserId(authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(userId, orderDTO));
    }

    @GetMapping

    public ResponseEntity<Page<?>> getAllOrders(Pageable pageable) {
        return ResponseEntity.ok(orderService.getAllOrders(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/user/orders")

    public ResponseEntity<Page<?>> getUserOrders(Authentication authentication, Pageable pageable) {
        Long userId = extractUserId(authentication);
        return ResponseEntity.ok(orderService.getUserOrders(userId, pageable));
    }

    @PutMapping("/{id}/status")

    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }

    @DeleteMapping("/{id}")

    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }

    private Long extractUserId(Authentication authentication) {
        // Extract user ID from JWT token claims
        return ((CustomUserDetails) authentication.getPrincipal()).getUserId();
    }
}