package org.course.ecommerce.controller;

import org.course.ecommerce.dto.OrderDetailsDto;
import org.course.ecommerce.dto.PageDto;
import org.course.ecommerce.service.OrderDetailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orderdetails")
public class OrderDetailsController {

    private final OrderDetailService orderDetailService;

    public OrderDetailsController(OrderDetailService orderDetailService) {
        this.orderDetailService = orderDetailService;
    }

    // GET /api/orderdetails?page=1&size=15
    @GetMapping
    public ResponseEntity<PageDto<OrderDetailsDto>> getOrderDetailsPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {

        if (page < 1 || size <= 0)
            return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(orderDetailService.getOrderDetailsPage(page, size));
    }

    // GET /api/orderdetails/{orderId}
    @GetMapping("/{orderId}")
    public List<OrderDetailsDto> getByOrderId(@PathVariable int orderId) {
        return orderDetailService.getOrderDetailsByOrderId(orderId);
    }

    // GET /api/orderdetails/{orderId}/{productId}
    @GetMapping("/{orderId}/{productId}")
    public ResponseEntity<OrderDetailsDto> getOrderDetail(
            @PathVariable int orderId, @PathVariable int productId) {

        OrderDetailsDto detail = orderDetailService.getOrderDetail(orderId, productId);
        return detail != null ? ResponseEntity.ok(detail) : ResponseEntity.notFound().build();
    }

    // POST /api/orderdetails
    @PostMapping
    public ResponseEntity<Void> addOrderDetail(@RequestBody OrderDetailsDto orderDetails) {
        boolean created = orderDetailService.addOrderDetail(orderDetails);
        return created ? ResponseEntity.status(201).build() : ResponseEntity.badRequest().build();
    }

    // PUT /api/orderdetails
    @PutMapping
    public ResponseEntity<Void> updateOrderDetail(@RequestBody OrderDetailsDto orderDetails) {
        boolean updated = orderDetailService.updateOrderDetail(orderDetails);
        return updated ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // PUT /api/orderdetails/{orderId}/{productId}
    @PutMapping("/{orderId}/{productId}")
    public ResponseEntity<Void> updateOrderDetail(
            @PathVariable int orderId, @PathVariable int productId,
            @RequestBody OrderDetailsDto orderDetails) {

        if (orderId != orderDetails.getOrderId() || productId != orderDetails.getProductId())
            return ResponseEntity.badRequest().build();

        boolean updated = orderDetailService.updateOrderDetail(orderDetails);
        return updated ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // DELETE /api/orderdetails/{orderId}/{productId}
    @DeleteMapping("/{orderId}/{productId}")
    public ResponseEntity<Void> deleteOrderDetail(
            @PathVariable int orderId, @PathVariable int productId) {

        boolean deleted = orderDetailService.deleteOrderDetail(orderId, productId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
