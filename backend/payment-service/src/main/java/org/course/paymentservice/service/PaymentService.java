package org.course.paymentservice.service;

import org.course.paymentservice.dto.PaymentDto;

import java.util.List;
import java.util.Optional;

public interface PaymentService {

    PaymentDto createPayment(PaymentDto payment);

    PaymentDto updateStatus(int id, String status);

    Optional<PaymentDto> getPaymentById(int id);

    List<PaymentDto> getPaymentsByOrderId(int orderId);

    List<PaymentDto> getAllPayments();
}
