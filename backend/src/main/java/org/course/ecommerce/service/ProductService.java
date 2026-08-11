package org.course.ecommerce.service;

import org.course.ecommerce.dto.ProductDto;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    ProductDto create(ProductDto product);

    Optional<ProductDto> findById(int productId);

    List<ProductDto> findAll();

    void update(ProductDto product);

    void delete(int productId);
}
