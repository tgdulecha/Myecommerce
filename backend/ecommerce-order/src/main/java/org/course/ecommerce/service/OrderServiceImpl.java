package org.course.ecommerce.service;

import org.course.ecommerce.dto.OrderDto;
import org.course.ecommerce.dto.PageDto;
import org.course.ecommerce.entity.Orders;
import org.course.ecommerce.mapper.OrderMapper;
import org.course.ecommerce.repository.OrdersRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrdersRepository ordersRepository;

    public OrderServiceImpl(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    @Override
    public PageDto<OrderDto> getOrdersPage(int page, int pageSize) {
        if (page < 1 || pageSize < 1)
            throw new IllegalArgumentException("page and pageSize must be > 0");

        var result = ordersRepository.findAll(
                PageRequest.of(page - 1, pageSize, Sort.by("orderId")));

        List<OrderDto> content = result.getContent()
                .stream()
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());

        return new PageDto<>(content, page, pageSize, result.getTotalElements());
    }

    @Override
    public Optional<OrderDto> getOrderById(int orderId) {
        return ordersRepository.findById(orderId).map(OrderMapper::toDto);
    }

    @Override
    @Transactional
    public boolean addOrder(OrderDto order) {
        if (order == null)
            throw new IllegalArgumentException("Order cannot be null");

        Orders entity = OrderMapper.toEntity(order);
        entity.setOrderId(0);
        Orders saved = ordersRepository.save(entity);
        order.setOrderId(saved.getOrderId());
        return true;
    }

    @Override
    @Transactional
    public boolean updateOrder(OrderDto order) {
        if (order == null)
            throw new IllegalArgumentException("Order is null");

        if (!ordersRepository.existsById(order.getOrderId()))
            return false;

        ordersRepository.save(OrderMapper.toEntity(order));
        return true;
    }

    @Override
    @Transactional
    public boolean deleteOrder(int orderId) {
        if (!ordersRepository.existsById(orderId))
            return false;

        ordersRepository.deleteById(orderId);
        return true;
    }

    @Override
    public List<OrderDto> getAllOrders() {
        return ordersRepository.findAll()
                .stream()
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
    }
}
