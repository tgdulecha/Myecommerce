package org.course.ecommerce.dto;

import java.math.BigDecimal;

public class OrderDetailsDto {

    private Integer orderId;
    private Integer productId;
    private String productName;
    private BigDecimal unitPrice;
    private Short quantity;
    private Float discount;

    public OrderDetailsDto() {}

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public Short getQuantity() { return quantity; }
    public void setQuantity(Short quantity) { this.quantity = quantity; }

    public Float getDiscount() { return discount; }
    public void setDiscount(Float discount) { this.discount = discount; }
}
