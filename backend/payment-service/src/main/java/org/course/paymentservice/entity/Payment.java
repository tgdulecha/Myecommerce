package org.course.paymentservice.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Unlike Account above, Payments is not inherited from the Northwind monolith -
 * this table exists only here. payment-service is the sole owner and sole writer,
 * which is what every table in this migration should look like once Phase 2 (giving
 * each service its own database) actually happens. orderId/customerEmail are kept
 * as plain scalars rather than JPA relationships - payment-service must not reach
 * into ecommerce-service's or auth-service's tables to resolve them.
 */
@Entity
@Table(name = "Payments", schema = "dbo")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PaymentID")
    private int paymentId;

    @Column(name = "OrderID", nullable = false)
    private int orderId;

    @Column(name = "CustomerEmail", nullable = false)
    private String customerEmail;

    @Column(name = "Amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "Method", nullable = false)
    private String method;

    @Column(name = "Status", nullable = false)
    private String status;

    @Column(name = "TransactionDate")
    private LocalDateTime transactionDate;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    public Payment() {}

    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
