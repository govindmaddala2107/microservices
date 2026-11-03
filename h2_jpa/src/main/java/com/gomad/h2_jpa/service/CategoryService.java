package com.gomad.h2_jpa.service;

import com.gomad.h2_jpa.model.Category;
import com.gomad.h2_jpa.payload.CategoryDTO;
import com.gomad.h2_jpa.payload.CategoryResponse;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

public interface CategoryService {
    CategoryResponse getAllCategories();
    CategoryResponse getAllCategoriesPagination(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO);
    CategoryDTO deleteCategory(Long id);
    CategoryDTO getCategoryById(Long id);
}
