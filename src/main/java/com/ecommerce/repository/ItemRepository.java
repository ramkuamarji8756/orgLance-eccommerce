package com.ecommerce.repository;



import com.ecommerce.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;

public interface ItemRepository extends JpaRepository<Item, Long> {
 Page<Item> findByNameContainingIgnoreCaseAndActiveTrue(String name, Pageable pageable);
 Page<Item> findByPriceBetweenAndActiveTrue(BigDecimal min, BigDecimal max, Pageable pageable);
}
