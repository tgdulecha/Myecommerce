package org.course.paymentservice.service;

import org.course.paymentservice.dto.PaymentDto;
import org.course.paymentservice.entity.Payment;
import org.course.paymentservice.exception.NotFoundException;
import org.course.paymentservice.mapper.PaymentMapper;
import org.course.paymentservice.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PaymentServiceImpl implements PaymentService {

    // A payment is a financial record, not a CRUD resource - it is created once and
    // only ever moves forward through this state machine. There is no update/delete
    // for its amount, method or order, and no endpoint to hard-delete a row.
    private static final Set<String> VALID_STATUSES = Set.of("PENDING", "COMPLETED", "FAILED", "REFUNDED");

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional
    public PaymentDto createPayment(PaymentDto payment) {
        if (payment.getOrderId() <= 0)
            throw new IllegalArgumentException("Order ID must be valid.");

        if (payment.getCustomerEmail() == null || payment.getCustomerEmail().isBlank())
            throw new IllegalArgumentException("Customer email is required.");

        if (payment.getAmount() == null || payment.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Amount must be greater than zero.");

        if (payment.getMethod() == null || payment.getMethod().isBlank())
            throw new IllegalArgumentException("Payment method is required.");

        Payment entity = PaymentMapper.toEntity(payment);
        entity.setPaymentId(0);
        entity.setStatus("PENDING");
        entity.setTransactionDate(LocalDateTime.now());
        entity.setCreatedAt(LocalDateTime.now());

        Payment saved = paymentRepository.save(entity);
        return PaymentMapper.toDto(saved);
    }

    @Override
    @Transactional
    public PaymentDto updateStatus(int id, String status) {
        if (status == null || !VALID_STATUSES.contains(status.toUpperCase()))
            throw new IllegalArgumentException("Status must be one of " + VALID_STATUSES);

        Payment entity = paymentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Payment not found: " + id));

        entity.setStatus(status.toUpperCase());
        entity.setTransactionDate(LocalDateTime.now());

        return PaymentMapper.toDto(paymentRepository.save(entity));
    }

    @Override
    public Optional<PaymentDto> getPaymentById(int id) {
        if (id <= 0) return Optional.empty();
        return paymentRepository.findById(id).map(PaymentMapper::toDto);
    }

    @Override
    public List<PaymentDto> getPaymentsByOrderId(int orderId) {
        if (orderId <= 0) return List.of();
        return PaymentMapper.toDtoList(paymentRepository.findByOrderId(orderId));
    }

    @Override
    public List<PaymentDto> getAllPayments() {
        return PaymentMapper.toDtoList(paymentRepository.findAll());
    }
}
