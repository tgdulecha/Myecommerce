package org.course.ecommerce.service;

import org.course.ecommerce.dto.ProductDto;
import org.course.ecommerce.entity.Product;
import org.course.ecommerce.exception.NotFoundException;
import org.course.ecommerce.mapper.ProductMapper;
import org.course.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public ProductDto create(ProductDto product) {
        validate(product);

        Product entity = ProductMapper.toEntity(product);
        entity.setProductID(0);
        Product saved = productRepository.save(entity);

        product.setProductId(saved.getProductID());
        return product;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductDto> findById(int productId) {
        if (productId <= 0) return Optional.empty();

        return productRepository.findById(productId).map(ProductMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> findAll() {
        return productRepository.findAll()
                .stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void update(ProductDto product) {
        if (product == null || product.getProductId() <= 0)
            throw new IllegalArgumentException("Valid product ID required");

        if (!productRepository.existsById(product.getProductId()))
            throw new NotFoundException("Product not found for update");

        productRepository.save(ProductMapper.toEntity(product));
    }

    @Override
    @Transactional
    public void delete(int productId) {
        if (productId <= 0)
            throw new IllegalArgumentException("Valid product ID required");

        if (!productRepository.existsById(productId))
            throw new NotFoundException("Product not found for deletion");

        productRepository.deleteById(productId);
    }

    private void validate(ProductDto product) {
        if (product == null)
            throw new IllegalArgumentException("Product must not be null");

        if (product.getProductName() == null || product.getProductName().isBlank())
            throw new IllegalArgumentException("Product name is required");
    }
}
