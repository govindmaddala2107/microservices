package com.gomad.h2_jpa.implementation;

import com.gomad.h2_jpa.exceptions.APIException;
import com.gomad.h2_jpa.exceptions.ResourceNotFoundException;
import com.gomad.h2_jpa.model.Category;
import com.gomad.h2_jpa.payload.CategoryDTO;
import com.gomad.h2_jpa.payload.CategoryResponse;
import com.gomad.h2_jpa.repository.CategoryRepository;
import com.gomad.h2_jpa.service.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImplementation implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategories() {
        List<Category> categories = categoryRepository.findAll();

        List<CategoryDTO> categoryDTOS = categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .toList();

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setCategories(categoryDTOS);
        return categoryResponse;
    }

    @Override
    public CategoryResponse getAllCategoriesPagination(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        List<Category> categories = categoryPage.getContent();

        List<CategoryDTO> categoryDTOS = categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .toList();

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setCategories(categoryDTOS);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());

        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        CategoryDTO existingCategory = categoryRepository.findByCategoryName(categoryDTO.getCategoryName());
        if(existingCategory != null){
            throw new APIException("Category with name " + categoryDTO.getCategoryName() + " already exists !!!");
        }
        // In case we want to return DTO then
        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category", "CategoryId", id);
        }

        category.setId(id);
        Category updatedCategory = categoryRepository.save(category);
        return modelMapper.map(updatedCategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO deleteCategory(Long id) {
        Category categoryToDelete = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", id));

        categoryRepository.delete(categoryToDelete);
        return modelMapper.map(categoryToDelete, CategoryDTO.class);
    }

    @Override
    public CategoryDTO getCategoryById(Long id) {
        Category categoryFound = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", id));
        return modelMapper.map(categoryFound, CategoryDTO.class);
    }
}
