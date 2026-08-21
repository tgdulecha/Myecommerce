package org.course.ecommerce.service;

import org.course.ecommerce.dto.ProductDto;
import org.course.ecommerce.entity.Product;
import org.course.ecommerce.exception.NotFoundException;
import org.course.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        productService = new ProductServiceImpl(productRepository);
    }

    private ProductDto validProduct() {
        ProductDto dto = new ProductDto();
        dto.setProductName("Chai");
        return dto;
    }

    @Test
    void createRejectsBlankName() {
        ProductDto dto = validProduct();
        dto.setProductName(" ");

        assertThatThrownBy(() -> productService.create(dto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createSavesAndReturnsGeneratedId() {
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> {
            Product entity = inv.getArgument(0);
            entity.setProductID(7);
            return entity;
        });

        ProductDto result = productService.create(validProduct());

        assertThat(result.getProductId()).isEqualTo(7);
    }

    @Test
    void findByIdReturnsEmptyForNonPositiveId() {
        assertThat(productService.findById(0)).isEmpty();
        verifyNoInteractions(productRepository);
    }

    @Test
    void updateThrowsWhenProductIdMissing() {
        ProductDto dto = validProduct();
        dto.setProductId(0);

        assertThatThrownBy(() -> productService.update(dto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateThrowsWhenNotFound() {
        ProductDto dto = validProduct();
        dto.setProductId(9);
        when(productRepository.existsById(9)).thenReturn(false);

        assertThatThrownBy(() -> productService.update(dto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void deleteThrowsWhenNotFound() {
        when(productRepository.existsById(9)).thenReturn(false);

        assertThatThrownBy(() -> productService.delete(9))
                .isInstanceOf(NotFoundException.class);

        verify(productRepository, never()).deleteById(any());
    }

    @Test
    void deleteDeletesWhenFound() {
        when(productRepository.existsById(9)).thenReturn(true);

        productService.delete(9);

        verify(productRepository).deleteById(9);
    }
}
