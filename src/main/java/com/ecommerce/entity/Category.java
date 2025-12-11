package com.ecommerce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	    
	    @Column(nullable = false, unique = true, length = 100)
	    private String name;
	    
	    @Column(length = 500)
	    private String description;
	    
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "parent_id")
	    private Category parent;
	    
	    @Column(nullable = false)
	    private Boolean active = true;
	    
	    @Column(nullable = false, updatable = false)
	    private LocalDateTime createdAt = LocalDateTime.now();
	    
	    @Column(nullable = false)
	    private LocalDateTime updatedAt = LocalDateTime.now();

}
