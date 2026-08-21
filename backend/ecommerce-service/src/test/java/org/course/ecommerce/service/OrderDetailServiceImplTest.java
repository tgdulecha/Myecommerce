package org.course.ecommerce.service;

import jakarta.persistence.EntityManager;
import org.course.ecommerce.dto.OrderDetailsDto;
import org.course.ecommerce.entity.OrderDetails;
import org.course.ecommerce.entity.OrderDetailsId;
import org.course.ecommerce.repository.OrderDetailsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderDetailServiceImplTest {

    @Mock
    private OrderDetailsRepository orderDetailsRepository;

    @Mock
    private EntityManager entityManager;

    private OrderDetailServiceImpl orderDetailService;

    @BeforeEach
    void setUp() {
        orderDetailService = new OrderDetailServiceImpl(orderDetailsRepository, entityManager);
    }

    private OrderDetailsDto detail(int orderId, int productId) {
        OrderDetailsDto dto = new OrderDetailsDto();
        dto.setOrderId(orderId);
        dto.setProductId(productId);
        dto.setUnitPrice(BigDecimal.TEN);
        dto.setQuantity((short) 1);
        dto.setDiscount(0f);
        return dto;
    }

    @Test
    void addOrderDetailRejectsNull() {
        assertThatThrownBy(() -> orderDetailService.addOrderDetail(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addOrderDetailReturnsFalseWhenAlreadyExists() {
        when(orderDetailsRepository.existsById(new OrderDetailsId(1, 2))).thenReturn(true);

        assertThat(orderDetailService.addOrderDetail(detail(1, 2))).isFalse();
        verify(orderDetailsRepository, never()).save(any());
    }

    @Test
    void addOrderDetailSavesWhenNew() {
        when(orderDetailsRepository.existsById(new OrderDetailsId(1, 2))).thenReturn(false);

        assertThat(orderDetailService.addOrderDetail(detail(1, 2))).isTrue();
        verify(orderDetailsRepository).save(any(OrderDetails.class));
    }

    @Test
    void updateOrderDetailReturnsFalseWhenMissing() {
        when(orderDetailsRepository.existsById(new OrderDetailsId(1, 2))).thenReturn(false);

        assertThat(orderDetailService.updateOrderDetail(detail(1, 2))).isFalse();
        verify(orderDetailsRepository, never()).save(any());
    }

    @Test
    void deleteOrderDetailReturnsFalseWhenMissing() {
        when(orderDetailsRepository.existsById(new OrderDetailsId(1, 2))).thenReturn(false);

        assertThat(orderDetailService.deleteOrderDetail(1, 2)).isFalse();
        verify(orderDetailsRepository, never()).deleteById(any());
    }

    @Test
    void deleteOrderDetailReturnsTrueAndDeletesWhenFound() {
        when(orderDetailsRepository.existsById(new OrderDetailsId(1, 2))).thenReturn(true);

        assertThat(orderDetailService.deleteOrderDetail(1, 2)).isTrue();
        verify(orderDetailsRepository).deleteById(new OrderDetailsId(1, 2));
    }

    @Test
    void getOrderDetailReturnsNullWhenMissing() {
        when(orderDetailsRepository.findByIdWithProduct(new OrderDetailsId(1, 2)))
                .thenReturn(java.util.Optional.empty());

        assertThat(orderDetailService.getOrderDetail(1, 2)).isNull();
    }

    @Test
    void getOrderDetailsPageRejectsInvalidArgs() {
        assertThatThrownBy(() -> orderDetailService.getOrderDetailsPage(-1, 5))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> orderDetailService.getOrderDetailsPage(0, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
