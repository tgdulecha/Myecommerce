package org.course.ecommerce.service;

import org.course.ecommerce.dto.OrderDto;
import org.course.ecommerce.dto.PageDto;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    PageDto<OrderDto> getOrdersPage(int page, int pageSize);

    Optional<OrderDto> getOrderById(int orderId);

    boolean addOrder(OrderDto order);

    boolean updateOrder(OrderDto order);

    boolean deleteOrder(int orderId);

    List<OrderDto> getAllOrders();
}
