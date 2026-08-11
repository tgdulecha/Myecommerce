package org.course.ecommerce.mapper;

import org.course.ecommerce.dto.OrderDto;
import org.course.ecommerce.entity.Orders;

public final class OrderMapper {

    private OrderMapper() {}

    public static OrderDto toDto(Orders entity) {
        if (entity == null) return null;

        OrderDto dto = new OrderDto();
        dto.setOrderId(entity.getOrderId());
        dto.setCustomerId(entity.getCustomerId());
        dto.setEmployeeId(entity.getEmployeeId());
        dto.setOrderDate(entity.getOrderDate());
        dto.setRequiredDate(entity.getRequiredDate());
        dto.setShippedDate(entity.getShippedDate());
        dto.setShipVia(entity.getShipVia());
        dto.setFreight(entity.getFreight());
        dto.setShipName(entity.getShipName());
        dto.setShipAddress(entity.getShipAddress());
        dto.setShipCity(entity.getShipCity());
        dto.setShipRegion(entity.getShipRegion());
        dto.setShipPostalCode(entity.getShipPostalCode());
        dto.setShipCountry(entity.getShipCountry());
        return dto;
    }

    public static Orders toEntity(OrderDto dto) {
        if (dto == null) return null;

        Orders entity = new Orders();
        entity.setOrderId(dto.getOrderId());
        entity.setCustomerId(dto.getCustomerId());
        if (dto.getEmployeeId() != null) entity.setEmployeeId(dto.getEmployeeId());
        entity.setOrderDate(dto.getOrderDate());
        entity.setRequiredDate(dto.getRequiredDate());
        entity.setShippedDate(dto.getShippedDate());
        if (dto.getShipVia() != null) entity.setShipVia(dto.getShipVia());
        entity.setFreight(dto.getFreight());
        entity.setShipName(dto.getShipName());
        entity.setShipAddress(dto.getShipAddress());
        entity.setShipCity(dto.getShipCity());
        entity.setShipRegion(dto.getShipRegion());
        entity.setShipPostalCode(dto.getShipPostalCode());
        entity.setShipCountry(dto.getShipCountry());
        return entity;
    }
}
