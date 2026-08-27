package org.course.paymentservice.service;

import org.course.paymentservice.dto.PaymentDto;
import org.course.paymentservice.entity.Payment;
import org.course.paymentservice.exception.NotFoundException;
import org.course.paymentservice.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentServiceImpl(paymentRepository);
    }

    @Test
    void createPaymentRejectsNonPositiveAmount() {
        PaymentDto dto = new PaymentDto(10248, "jane@example.com", BigDecimal.ZERO, "CreditCard");

        assertThatThrownBy(() -> paymentService.createPayment(dto))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(paymentRepository);
    }

    @Test
    void createPaymentRejectsInvalidOrderId() {
        PaymentDto dto = new PaymentDto(0, "jane@example.com", BigDecimal.TEN, "CreditCard");

        assertThatThrownBy(() -> paymentService.createPayment(dto))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(paymentRepository);
    }

    @Test
    void createPaymentAlwaysStartsPending() {
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> {
            Payment entity = inv.getArgument(0);
            entity.setPaymentId(7);
            return entity;
        });

        PaymentDto dto = new PaymentDto(10248, "jane@example.com", BigDecimal.valueOf(49.99), "CreditCard");
        PaymentDto result = paymentService.createPayment(dto);

        assertThat(result.getPaymentId()).isEqualTo(7);
        assertThat(result.getStatus()).isEqualTo("PENDING");
        assertThat(result.getTransactionDate()).isNotNull();
    }

    @Test
    void updateStatusRejectsUnknownStatus() {
        assertThatThrownBy(() -> paymentService.updateStatus(1, "SHIPPED"))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(paymentRepository);
    }

    @Test
    void updateStatusThrowsWhenNotFound() {
        when(paymentRepository.findById(9)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.updateStatus(9, "COMPLETED"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateStatusSavesUppercasedStatus() {
        Payment existing = new Payment();
        existing.setPaymentId(3);
        existing.setStatus("PENDING");
        when(paymentRepository.findById(3)).thenReturn(Optional.of(existing));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        PaymentDto result = paymentService.updateStatus(3, "completed");

        assertThat(result.getStatus()).isEqualTo("COMPLETED");
    }

    @Test
    void getPaymentByIdReturnsEmptyForNonPositiveId() {
        assertThat(paymentService.getPaymentById(0)).isEmpty();
        verifyNoInteractions(paymentRepository);
    }
}
