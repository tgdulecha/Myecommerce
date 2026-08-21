package org.course.ecommerce.service;

import org.course.ecommerce.dto.CategoryDto;
import org.course.ecommerce.entity.Category;
import org.course.ecommerce.exception.NotFoundException;
import org.course.ecommerce.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryServiceImpl(categoryRepository);
    }

    @Test
    void createNewCategoryRejectsBlankName() {
        CategoryDto dto = new CategoryDto("  ", "some description");

        assertThatThrownBy(() -> categoryService.createNewCategory(dto))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(categoryRepository);
    }

    @Test
    void createNewCategorySavesAndReturnsGeneratedId() {
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> {
            Category entity = inv.getArgument(0);
            entity.setCategoryID(3);
            return entity;
        });

        CategoryDto dto = new CategoryDto("Beverages", "Soft drinks, coffees, teas");
        CategoryDto result = categoryService.createNewCategory(dto);

        assertThat(result.getCategoryID()).isEqualTo(3);
    }

    @Test
    void updateCategoryThrowsWhenIdMissing() {
        CategoryDto dto = new CategoryDto("Beverages", "desc");
        dto.setCategoryID(null);

        assertThatThrownBy(() -> categoryService.updateCategory(dto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateCategoryThrowsWhenNotFound() {
        CategoryDto dto = new CategoryDto("Beverages", "desc");
        dto.setCategoryID(9);
        when(categoryRepository.existsById(9)).thenReturn(false);

        assertThatThrownBy(() -> categoryService.updateCategory(dto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void deleteCategoryRejectsNonPositiveId() {
        assertThatThrownBy(() -> categoryService.deleteCategory(0))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(categoryRepository);
    }

    @Test
    void getCategoryByIDReturnsEmptyForNonPositiveId() {
        assertThat(categoryService.getCategoryByID(0)).isEmpty();
        verifyNoInteractions(categoryRepository);
    }

    @Test
    void getCategoryByIDReturnsEmptyWhenMissing() {
        when(categoryRepository.findById(5)).thenReturn(Optional.empty());

        assertThat(categoryService.getCategoryByID(5)).isEmpty();
    }
}
