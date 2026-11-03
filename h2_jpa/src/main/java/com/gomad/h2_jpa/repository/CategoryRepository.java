package com.gomad.h2_jpa.repository;

import com.gomad.h2_jpa.model.Category;
import com.gomad.h2_jpa.payload.CategoryDTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    CategoryDTO findByCategoryName(String categoryName);
}
