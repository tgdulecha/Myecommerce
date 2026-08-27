package org.course.paymentservice.mapper;

import org.course.paymentservice.dto.PaymentDto;
import org.course.paymentservice.entity.Payment;

import java.util.List;
import java.util.stream.Collectors;

public final class PaymentMapper {

    private PaymentMapper() {}

    public static PaymentDto toDto(Payment entity) {
        if (entity == null) return null;

        PaymentDto dto = new PaymentDto();
        dto.setPaymentId(entity.getPaymentId());
        dto.setOrderId(entity.getOrderId());
        dto.setCustomerEmail(entity.getCustomerEmail());
        dto.setAmount(entity.getAmount());
        dto.setMethod(entity.getMethod());
        dto.setStatus(entity.getStatus());
        dto.setTransactionDate(entity.getTransactionDate());
        return dto;
    }

    public static Payment toEntity(PaymentDto dto) {
        if (dto == null) return null;

        Payment entity = new Payment();
        if (dto.getPaymentId() != null) entity.setPaymentId(dto.getPaymentId());
        entity.setOrderId(dto.getOrderId());
        entity.setCustomerEmail(dto.getCustomerEmail());
        entity.setAmount(dto.getAmount());
        entity.setMethod(dto.getMethod());
        entity.setStatus(dto.getStatus());
        entity.setTransactionDate(dto.getTransactionDate());
        return entity;
    }

    public static List<PaymentDto> toDtoList(List<Payment> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(PaymentMapper::toDto).collect(Collectors.toList());
    }
}
