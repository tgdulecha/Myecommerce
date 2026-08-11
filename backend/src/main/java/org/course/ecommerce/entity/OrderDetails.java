package org.course.ecommerce.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "Order Details", schema = "dbo")
public class OrderDetails {

    @EmbeddedId
    private OrderDetailsId id;

    @Column(name = "UnitPrice")
    private BigDecimal unitPrice;

    @Column(name = "Quantity")
    private Short quantity;

    @Column(name = "Discount")
    private Float discount;

    // Read-only navigation shadowing the OrderID/ProductID columns already owned by the
    // embedded id - writes to the FK still go through setOrderId/setProductId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OrderID", insertable = false, updatable = false)
    private Orders order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ProductID", insertable = false, updatable = false)
    private Product product;

    public OrderDetails() {}

    public OrderDetails(Integer orderId, Integer productId, BigDecimal unitPrice, Short quantity, Float discount) {
        this.id = new OrderDetailsId(orderId, productId);
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.discount = discount;
    }

    public Integer getOrderId() { return id != null ? id.getOrderId() : null; }
    public void setOrderId(Integer orderId) {
        if (id == null) id = new OrderDetailsId();
        id.setOrderId(orderId);
    }

    public Integer getProductId() { return id != null ? id.getProductId() : null; }
    public void setProductId(Integer productId) {
        if (id == null) id = new OrderDetailsId();
        id.setProductId(productId);
    }

    public OrderDetailsId getId() { return id; }
    public void setId(OrderDetailsId id) { this.id = id; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public Short getQuantity() { return quantity; }
    public void setQuantity(Short quantity) { this.quantity = quantity; }

    public Float getDiscount() { return discount; }
    public void setDiscount(Float discount) { this.discount = discount; }

    public Orders getOrder() { return order; }
    public void setOrder(Orders order) { this.order = order; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}
