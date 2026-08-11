package org.course.ecommerce.mapper;

import org.course.ecommerce.dto.ProductDto;
import org.course.ecommerce.entity.Product;

public final class ProductMapper {

    private ProductMapper() {}

    public static ProductDto toDto(Product entity) {
        if (entity == null) return null;

        ProductDto dto = new ProductDto();
        dto.setProductId(entity.getProductID());
        dto.setProductName(entity.getProductName());
        dto.setSupplierId(entity.getSupplierID());
        dto.setCategoryId(entity.getCategoryID());
        if (entity.getCategory() != null) {
            dto.setCategoryName(entity.getCategory().getCategoryName());
        }
        dto.setQuantityPerUnit(entity.getQuantityPerUnit());
        dto.setUnitPrice(entity.getUnitPrice());
        dto.setUnitsInStock(entity.getUnitsInStock());
        dto.setUnitsOnOrder(entity.getUnitsOnOrder());
        dto.setReorderLevel(entity.getReorderLevel());
        dto.setDiscontinued(entity.isDiscontinued());
        return dto;
    }

    public static Product toEntity(ProductDto dto) {
        if (dto == null) return null;

        Product entity = new Product();
        entity.setProductID(dto.getProductId());
        entity.setProductName(dto.getProductName());
        entity.setSupplierID(dto.getSupplierId());
        entity.setCategoryID(dto.getCategoryId());
        entity.setQuantityPerUnit(dto.getQuantityPerUnit());
        entity.setUnitPrice(dto.getUnitPrice());
        if (dto.getUnitsInStock() != null) entity.setUnitsInStock(dto.getUnitsInStock());
        if (dto.getUnitsOnOrder() != null) entity.setUnitsOnOrder(dto.getUnitsOnOrder());
        if (dto.getReorderLevel() != null) entity.setReorderLevel(dto.getReorderLevel());
        entity.setDiscontinued(dto.isDiscontinued());
        return entity;
    }
}
