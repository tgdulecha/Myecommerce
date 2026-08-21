package org.course.ecommerce.service;

import org.course.ecommerce.dto.CategoryDto;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    CategoryDto createNewCategory(CategoryDto category);

    void updateCategory(CategoryDto category);

    void deleteCategory(int id);

    List<CategoryDto> getAllCategories();

    Optional<CategoryDto> getCategoryByID(int id);
}
