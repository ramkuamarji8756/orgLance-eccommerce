package com.ecommerce.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

//import org.antlr.v4.runtime.misc.NotNull;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {
    
    private Long id;
    
    @NotBlank(message = "Item name is required")
    @Size(min = 3, max = 255, message = "Item name must be between 3 and 255 characters")
    private String name;
    
    private String description;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;
    
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;
    
    private String imageUrl;
    
    @DecimalMin(value = "0.0", message = "Tax cannot be negative")
    private BigDecimal tax;
    
    private Boolean active;
    
    private Set<Long> categoryIds;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

