package org.course.ecommerce.controller;

import org.course.ecommerce.dto.OrderDto;
import org.course.ecommerce.dto.PageDto;
import org.course.ecommerce.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

// Base URL: http://localhost:8081/api/orders
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // GET /api/orders/all
    @GetMapping("/all")
    public List<OrderDto> getAllOrders() {
        return orderService.getAllOrders();
    }

    // GET /api/orders?page=1&size=5
    @GetMapping
    public ResponseEntity<PageDto<OrderDto>> getOrdersPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {

        if (page < 1 || size < 1)
            return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(orderService.getOrdersPage(page, size));
    }

    // GET /api/orders/{id}
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable int id) {
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST /api/orders
    @PostMapping
    public ResponseEntity<Void> createOrder(@RequestBody OrderDto order) {
        if (order == null)
            return ResponseEntity.badRequest().build();

        orderService.addOrder(order);
        URI location = URI.create("/api/orders/" + order.getOrderId());
        return ResponseEntity.created(location).build();
    }

    // PUT /api/orders/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateOrder(@PathVariable int id, @RequestBody OrderDto order) {
        if (order == null)
            return ResponseEntity.badRequest().build();

        order.setOrderId(id);
        boolean updated = orderService.updateOrder(order);
        return updated ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // DELETE /api/orders/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable int id) {
        boolean deleted = orderService.deleteOrder(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
