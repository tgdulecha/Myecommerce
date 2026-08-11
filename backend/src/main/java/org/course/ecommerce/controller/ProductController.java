package org.course.ecommerce.controller;

import org.course.ecommerce.dto.ProductDto;
import org.course.ecommerce.exception.NotFoundException;
import org.course.ecommerce.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

// URL: http://localhost:8081/api/products
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductDto> getAllProducts() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable int id) {
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Void> createProduct(@RequestBody ProductDto product) {
        productService.create(product);
        URI location = URI.create("/api/products/" + product.getProductId());
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProduct(@PathVariable int id, @RequestBody ProductDto product) {
        if (id != product.getProductId())
            return ResponseEntity.badRequest().build();

        if (productService.findById(id).isEmpty())
            return ResponseEntity.notFound().build();

        productService.update(product);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable int id) {
        try {
            productService.delete(id);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
