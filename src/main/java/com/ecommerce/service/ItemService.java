package com.ecommerce.service;

import com.ecommerce.entity.Item;
import com.ecommerce.entity.Category;
import com.ecommerce.dto.ItemDTO;
import com.ecommerce.dto.ItemResponseDTO;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.ItemRepository;
import com.ecommerce.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemService {
    
    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    
    public ItemResponseDTO createItem(ItemDTO dto) {
        Item item = new Item();
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        item.setStock(dto.getStock());
        item.setImageUrl(dto.getImageUrl());
        item.setTax(dto.getTax() != null ? dto.getTax() : BigDecimal.ZERO);
        item.setActive(dto.getActive() != null ? dto.getActive() : true);
        
        if (dto.getCategoryIds() != null && !dto.getCategoryIds().isEmpty()) {
            Set<Category> categories = dto.getCategoryIds().stream()
                .map(id -> categoryRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id)))
                .collect(Collectors.toSet());
            item.setCategories(categories);
        }
        
        Item saved = itemRepository.save(item);
        return mapToResponseDTO(saved);
    }
    
    public ItemResponseDTO getItemById(Long id) {
        Item item = itemRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Item not found: " + id));
        return mapToResponseDTO(item);
    }
    
    public Page<ItemResponseDTO> getAllItems(Pageable pageable) {
        return itemRepository.findAll(pageable)
            .map(this::mapToResponseDTO);
    }
    
    public Page<ItemResponseDTO> searchItems(String name, BigDecimal minPrice, 
                                             BigDecimal maxPrice, Long categoryId, 
                                             Pageable pageable) {
        // Implementation using custom query methods
        if (name != null && !name.isEmpty()) {
            //return itemRepository.findByNameContainingIgnaseCaseAndActiveTrue(name, pageable)
        	 return itemRepository.findByNameContainingIgnoreCaseAndActiveTrue(name, pageable)
                .map(this::mapToResponseDTO);
        }
        return itemRepository.findAll(pageable)
            .map(this::mapToResponseDTO);
    }
    
    public ItemResponseDTO updateItem(Long id, ItemDTO dto) {
        Item item = itemRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Item not found: " + id));
        
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        item.setStock(dto.getStock());
        item.setImageUrl(dto.getImageUrl());
        item.setTax(dto.getTax());
        item.setActive(dto.getActive());
        item.setUpdatedAt(LocalDateTime.now());
        
        if (dto.getCategoryIds() != null) {
            Set<Category> categories = dto.getCategoryIds().stream()
                .map(catId -> categoryRepository.findById(catId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + catId)))
                .collect(Collectors.toSet());
            item.setCategories(categories);
        }
        
        Item updated = itemRepository.save(item);
        return mapToResponseDTO(updated);
    }
    
    public void deleteItem(Long id) {
        Item item = itemRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Item not found: " + id));
        itemRepository.delete(item);
    }
    
    private ItemResponseDTO mapToResponseDTO(Item item) {
        ItemResponseDTO dto = new ItemResponseDTO();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setPrice(item.getPrice());
        dto.setStock(item.getStock());
        dto.setImageUrl(item.getImageUrl());
        dto.setTax(item.getTax());
        dto.setActive(item.getActive());
        dto.setCreatedAt(item.getCreatedAt());
        dto.setUpdatedAt(item.getUpdatedAt());
        // Map categories if needed
        return dto;
    }
}