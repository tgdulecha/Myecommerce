package org.course.ecommerce.controller;

import org.course.ecommerce.dto.CategoryDto;
import org.course.ecommerce.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

// URL: http://localhost:8081/api/category
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryDto> getAllCategory() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryByID(@PathVariable int id) {
        return categoryService.getCategoryByID(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Void> createNewCategory(@RequestBody CategoryDto category) {
        categoryService.createNewCategory(category);
        URI location = URI.create("/api/category/" + category.getCategoryID());
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCategory(@PathVariable int id, @RequestBody CategoryDto category) {
        if (category.getCategoryID() == null || id != category.getCategoryID())
            return ResponseEntity.badRequest().build();

        if (categoryService.getCategoryByID(id).isEmpty())
            return ResponseEntity.notFound().build();

        categoryService.updateCategory(category);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable int id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
