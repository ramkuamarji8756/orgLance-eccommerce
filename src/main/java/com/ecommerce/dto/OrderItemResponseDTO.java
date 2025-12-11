package com.ecommerce.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
class OrderItemResponseDTO {
    
    private Long id;
    private ItemResponseDTO item;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal tax;
    private BigDecimal subtotal;
}
