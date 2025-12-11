package com.ecommerce.service;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderItem;
import com.ecommerce.entity.Item;
import com.ecommerce.entity.User;
import com.ecommerce.dto.OrderDTO;
import com.ecommerce.dto.OrderItemDTO;
import com.ecommerce.dto.OrderResponseDTO;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ItemRepository;
import com.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    
    public OrderResponseDTO createOrder(Long userId, OrderDTO dto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BadRequestException("Order must contain at least one item");
        }
        
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(dto.getShippingAddress());
        order.setNotes(dto.getNotes());
        order.setStatus(Order.OrderStatus.PENDING);
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal taxAmount = BigDecimal.ZERO;
        
        for (OrderItemDTO itemDTO : dto.getItems()) {
            Item item = itemRepository.findById(itemDTO.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Item not found: " + itemDTO.getItemId()));
            
            if (item.getStock() < itemDTO.getQuantity()) {
                throw new BadRequestException("Insufficient stock for item: " + item.getName());
            }
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setItem(item);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setPrice(item.getPrice());
            orderItem.setTax(item.getTax());
            
            BigDecimal subtotal = item.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity()));
            orderItem.setSubtotal(subtotal);
            
            order.getItems().add(orderItem);
            
            totalAmount = totalAmount.add(subtotal);
            taxAmount = taxAmount.add(item.getTax().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));
            
            // Decrease stock
            item.setStock(item.getStock() - itemDTO.getQuantity());
        }
        
        order.setTotalAmount(totalAmount);
        order.setTaxAmount(taxAmount);
        order.setFinalAmount(totalAmount.add(taxAmount));
        
        Order saved = orderRepository.save(order);
        return mapToResponseDTO(saved);
    }
    
    public OrderResponseDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
        return mapToResponseDTO(order);
    }
    
    public Page<OrderResponseDTO> getUserOrders(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable)
            .map(this::mapToResponseDTO);
    }
    
    public Page<OrderResponseDTO> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
            .map(this::mapToResponseDTO);
    }
    
    public OrderResponseDTO updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
        
        try {
            order.setStatus(Order.OrderStatus.valueOf(status));
            order.setUpdatedAt(LocalDateTime.now());
            Order updated = orderRepository.save(order);
            return mapToResponseDTO(updated);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid order status: " + status);
        }
    }
    
    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
        
        if (order.getStatus() == Order.OrderStatus.CANCELLED ||
            order.getStatus() == Order.OrderStatus.DELIVERED) {
            throw new BadRequestException("Cannot cancel order with status: " + order.getStatus());
        }
        
        // Restore item stock
        for (OrderItem item : order.getItems()) {
            Item product = item.getItem();
            product.setStock(product.getStock() + item.getQuantity());
        }
        
        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }
    
    private OrderResponseDTO mapToResponseDTO(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setTaxAmount(order.getTaxAmount());
        dto.setFinalAmount(order.getFinalAmount());
        dto.setStatus(order.getStatus().toString());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setNotes(order.getNotes());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        // Map items and user
        return dto;
    }
}



