package org.course.ecommerce.mapper;

import org.course.ecommerce.dto.CategoryDto;
import org.course.ecommerce.entity.Category;

import java.util.List;
import java.util.stream.Collectors;

public final class CategoryMapper {

    private CategoryMapper() {}

    public static CategoryDto toDto(Category entity) {
        if (entity == null) return null;

        CategoryDto dto = new CategoryDto();
        dto.setCategoryID(entity.getCategoryID());
        dto.setCategoryName(entity.getCategoryName());
        dto.setDescription(entity.getDescription());
        return dto;
    }

    public static Category toEntity(CategoryDto dto) {
        if (dto == null) return null;

        Category entity = new Category();
        if (dto.getCategoryID() != null) entity.setCategoryID(dto.getCategoryID());
        entity.setCategoryName(dto.getCategoryName());
        entity.setDescription(dto.getDescription());
        return entity;
    }

    public static List<CategoryDto> toDtoList(List<Category> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(CategoryMapper::toDto).collect(Collectors.toList());
    }
}
