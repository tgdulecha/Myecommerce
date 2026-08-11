package org.course.ecommerce.mapper;

import org.course.ecommerce.dto.OrderDetailsDto;
import org.course.ecommerce.entity.OrderDetails;

import java.util.List;
import java.util.stream.Collectors;

public final class OrderDetailsMapper {

    private OrderDetailsMapper() {}

    public static OrderDetailsDto toDto(OrderDetails entity) {
        if (entity == null) return null;

        OrderDetailsDto dto = new OrderDetailsDto();
        dto.setOrderId(entity.getOrderId());
        dto.setProductId(entity.getProductId());
        dto.setProductName(entity.getProduct() != null ? entity.getProduct().getProductName() : null);
        dto.setUnitPrice(entity.getUnitPrice());
        dto.setQuantity(entity.getQuantity());
        dto.setDiscount(entity.getDiscount());
        return dto;
    }

    public static OrderDetails toEntity(OrderDetailsDto dto) {
        if (dto == null) return null;

        return new OrderDetails(
                dto.getOrderId(),
                dto.getProductId(),
                dto.getUnitPrice(),
                dto.getQuantity(),
                dto.getDiscount()
        );
    }

    public static List<OrderDetailsDto> toDtoList(List<OrderDetails> entities) {
        return entities.stream().map(OrderDetailsMapper::toDto).collect(Collectors.toList());
    }
}
