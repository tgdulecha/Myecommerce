package org.course.paymentservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentDto {

    private Integer paymentId;
    private int orderId;
    private String customerEmail;
    private BigDecimal amount;
    private String method;
    private String status;
    private LocalDateTime transactionDate;

    public PaymentDto() {}

    public PaymentDto(int orderId, String customerEmail, BigDecimal amount, String method) {
        this.orderId = orderId;
        this.customerEmail = customerEmail;
        this.amount = amount;
        this.method = method;
    }

    public Integer getPaymentId() { return paymentId; }
    public void setPaymentId(Integer paymentId) { this.paymentId = paymentId; }

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
}
