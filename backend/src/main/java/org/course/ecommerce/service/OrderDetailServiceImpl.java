package org.course.ecommerce.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.course.ecommerce.dto.OrderDetailsDto;
import org.course.ecommerce.dto.PageDto;
import org.course.ecommerce.entity.OrderDetails;
import org.course.ecommerce.entity.OrderDetailsId;
import org.course.ecommerce.mapper.OrderDetailsMapper;
import org.course.ecommerce.repository.OrderDetailsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderDetailServiceImpl implements OrderDetailService {

    private final OrderDetailsRepository orderDetailsRepository;
    private final EntityManager entityManager;

    public OrderDetailServiceImpl(OrderDetailsRepository orderDetailsRepository, EntityManager entityManager) {
        this.orderDetailsRepository = orderDetailsRepository;
        this.entityManager = entityManager;
    }

    // NOTE: mirrors the original OrderDetailServiceImpl, which forwards "page" straight
    // through as the query offset (no page-1 conversion) unlike OrderServiceImpl.
    @Override
    public PageDto<OrderDetailsDto> getOrderDetailsPage(int page, int pageSize) {
        if (page < 0 || pageSize < 1)
            throw new IllegalArgumentException("page and pageSize must be greater than 0");

        int offset = page;

        TypedQuery<OrderDetails> query = entityManager.createQuery(
                "SELECT od FROM OrderDetails od JOIN FETCH od.product ORDER BY od.id.orderId, od.id.productId", OrderDetails.class);
        query.setFirstResult(offset);
        query.setMaxResults(pageSize);
        List<OrderDetails> data = query.getResultList();

        long totalElements = entityManager.createQuery(
                "SELECT COUNT(od) FROM OrderDetails od", Long.class).getSingleResult();

        return new PageDto<>(OrderDetailsMapper.toDtoList(data), offset / pageSize + 1, pageSize, totalElements);
    }

    @Override
    public OrderDetailsDto getOrderDetail(int orderId, int productId) {
        return orderDetailsRepository.findByIdWithProduct(new OrderDetailsId(orderId, productId))
                .map(OrderDetailsMapper::toDto)
                .orElse(null);
    }

    @Override
    @Transactional
    public boolean addOrderDetail(OrderDetailsDto orderDetails) {
        if (orderDetails == null)
            throw new IllegalArgumentException("OrderDetails cannot be null");

        OrderDetailsId id = new OrderDetailsId(orderDetails.getOrderId(), orderDetails.getProductId());
        if (orderDetailsRepository.existsById(id))
            return false;

        orderDetailsRepository.save(OrderDetailsMapper.toEntity(orderDetails));
        return true;
    }

    @Override
    @Transactional
    public boolean updateOrderDetail(OrderDetailsDto orderDetails) {
        if (orderDetails == null)
            throw new IllegalArgumentException("OrderDetails cannot be null");

        OrderDetailsId id = new OrderDetailsId(orderDetails.getOrderId(), orderDetails.getProductId());
        if (!orderDetailsRepository.existsById(id))
            return false;

        orderDetailsRepository.save(OrderDetailsMapper.toEntity(orderDetails));
        return true;
    }

    @Override
    @Transactional
    public boolean deleteOrderDetail(int orderId, int productId) {
        OrderDetailsId id = new OrderDetailsId(orderId, productId);
        if (!orderDetailsRepository.existsById(id))
            return false;

        orderDetailsRepository.deleteById(id);
        return true;
    }

    @Override
    public List<OrderDetailsDto> getOrderDetailsByOrderId(int orderId) {
        return OrderDetailsMapper.toDtoList(orderDetailsRepository.findByIdOrderId(orderId));
    }
}
