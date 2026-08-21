package org.course.ecommerce.service;

import org.course.ecommerce.dto.OrderDetailsDto;
import org.course.ecommerce.dto.PageDto;

import java.util.List;

public interface OrderDetailService {

    PageDto<OrderDetailsDto> getOrderDetailsPage(int page, int pageSize);

    OrderDetailsDto getOrderDetail(int orderId, int productId);

    boolean addOrderDetail(OrderDetailsDto orderDetails);

    boolean updateOrderDetail(OrderDetailsDto orderDetails);

    boolean deleteOrderDetail(int orderId, int productId);

    List<OrderDetailsDto> getOrderDetailsByOrderId(int orderId);
}
