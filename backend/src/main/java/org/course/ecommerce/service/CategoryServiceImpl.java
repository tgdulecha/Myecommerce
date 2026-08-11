package org.course.ecommerce.service;

import org.course.ecommerce.dto.CategoryDto;
import org.course.ecommerce.entity.Category;
import org.course.ecommerce.exception.NotFoundException;
import org.course.ecommerce.mapper.CategoryMapper;
import org.course.ecommerce.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public CategoryDto createNewCategory(CategoryDto category) {
        if (category.getCategoryName() == null || category.getCategoryName().trim().isEmpty())
            throw new IllegalArgumentException("Category name cannot be empty.");

        Category entity = CategoryMapper.toEntity(category);
        entity.setCategoryID(0);
        Category saved = categoryRepository.save(entity);

        category.setCategoryID(saved.getCategoryID());
        return category;
    }

    @Override
    @Transactional
    public void updateCategory(CategoryDto category) {
        if (category.getCategoryID() == null || category.getCategoryID() <= 0)
            throw new IllegalArgumentException("Category ID must be valid for update.");

        if (!categoryRepository.existsById(category.getCategoryID()))
            throw new NotFoundException("Category not found for update.");

        categoryRepository.save(CategoryMapper.toEntity(category));
    }

    @Override
    @Transactional
    public void deleteCategory(int id) {
        if (id <= 0)
            throw new IllegalArgumentException("Category ID must be valid for delete.");

        categoryRepository.deleteById(id);
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        return CategoryMapper.toDtoList(categoryRepository.findAll());
    }

    @Override
    public Optional<CategoryDto> getCategoryByID(int id) {
        if (id <= 0) return Optional.empty();
        return categoryRepository.findById(id).map(CategoryMapper::toDto);
    }
}
