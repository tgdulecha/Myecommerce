package org.course.ecommerce.service;

import org.course.ecommerce.dto.OrderDto;
import org.course.ecommerce.dto.PageDto;
import org.course.ecommerce.entity.Orders;
import org.course.ecommerce.repository.OrdersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrdersRepository ordersRepository;

    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(ordersRepository);
    }

    @Test
    void getOrdersPageRejectsNonPositivePageOrSize() {
        assertThatThrownBy(() -> orderService.getOrdersPage(0, 5))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> orderService.getOrdersPage(1, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getOrdersPageConvertsOneBasedPageToZeroBasedOffset() {
        Orders order = new Orders();
        order.setOrderId(1);
        when(ordersRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(order), PageRequest.of(1, 5), 11));

        PageDto<OrderDto> result = orderService.getOrdersPage(2, 5);

        assertThat(result.getPage()).isEqualTo(2);
        assertThat(result.getPageSize()).isEqualTo(5);
        assertThat(result.getTotalElements()).isEqualTo(11);
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void addOrderAssignsGeneratedIdBackOntoDto() {
        when(ordersRepository.save(any(Orders.class))).thenAnswer(inv -> {
            Orders entity = inv.getArgument(0);
            entity.setOrderId(101);
            return entity;
        });

        OrderDto dto = new OrderDto();
        boolean result = orderService.addOrder(dto);

        assertThat(result).isTrue();
        assertThat(dto.getOrderId()).isEqualTo(101);
    }

    @Test
    void addOrderRejectsNull() {
        assertThatThrownBy(() -> orderService.addOrder(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateOrderReturnsFalseWhenMissing() {
        OrderDto dto = new OrderDto();
        dto.setOrderId(5);
        when(ordersRepository.existsById(5)).thenReturn(false);

        assertThat(orderService.updateOrder(dto)).isFalse();
        verify(ordersRepository, never()).save(any());
    }

    @Test
    void deleteOrderReturnsFalseWhenMissing() {
        when(ordersRepository.existsById(5)).thenReturn(false);

        assertThat(orderService.deleteOrder(5)).isFalse();
        verify(ordersRepository, never()).deleteById(any());
    }

    @Test
    void deleteOrderReturnsTrueAndDeletesWhenFound() {
        when(ordersRepository.existsById(5)).thenReturn(true);

        assertThat(orderService.deleteOrder(5)).isTrue();
        verify(ordersRepository).deleteById(5);
    }

    @Test
    void getOrderByIdReturnsEmptyWhenMissing() {
        when(ordersRepository.findById(5)).thenReturn(Optional.empty());

        assertThat(orderService.getOrderById(5)).isEmpty();
    }
}
