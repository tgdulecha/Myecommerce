package org.course.paymentservice.controller;

import org.course.paymentservice.dto.PaymentDto;
import org.course.paymentservice.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

// Base URL: http://localhost:8084/api/payments
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // GET /api/payments  or  /api/payments?orderId=10248
    @GetMapping
    public List<PaymentDto> getPayments(@RequestParam(required = false) Integer orderId) {
        return orderId == null
                ? paymentService.getAllPayments()
                : paymentService.getPaymentsByOrderId(orderId);
    }

    // GET /api/payments/{id}
    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getPaymentById(@PathVariable int id) {
        return paymentService.getPaymentById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST /api/payments - always created as PENDING; use PATCH .../status to progress it
    @PostMapping
    public ResponseEntity<PaymentDto> createPayment(@RequestBody PaymentDto payment) {
        if (payment == null)
            return ResponseEntity.badRequest().build();

        PaymentDto created = paymentService.createPayment(payment);
        URI location = URI.create("/api/payments/" + created.getPaymentId());
        return ResponseEntity.created(location).body(created);
    }

    // PATCH /api/payments/{id}/status  body: {"status": "COMPLETED"}
    @PatchMapping("/{id}/status")
    public ResponseEntity<PaymentDto> updateStatus(@PathVariable int id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(paymentService.updateStatus(id, body.get("status")));
    }
}
