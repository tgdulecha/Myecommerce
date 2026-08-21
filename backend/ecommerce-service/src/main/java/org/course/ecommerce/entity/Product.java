package org.course.ecommerce.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "Products", schema = "dbo")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProductID")
    private int productID;

    @Column(name = "ProductName")
    private String productName;

    @Column(name = "SupplierID")
    private Integer supplierID;

    @Column(name = "CategoryID")
    private Integer categoryID;

    @Column(name = "QuantityPerUnit")
    private String quantityPerUnit;

    @Column(name = "UnitPrice")
    private BigDecimal unitPrice;

    @Column(name = "UnitsInStock")
    private short unitsInStock;

    @Column(name = "UnitsOnOrder")
    private short unitsOnOrder;

    @Column(name = "ReorderLevel")
    private short reorderLevel;

    @Column(name = "Discontinued")
    private boolean discontinued;

    // Read-only navigation shadowing categoryID - writes still go through setCategoryID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CategoryID", insertable = false, updatable = false)
    private Category category;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<OrderDetails> orderDetails;

    public Product() {}

    public int getProductID() { return productID; }
    public void setProductID(int productID) { this.productID = productID; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Integer getSupplierID() { return supplierID; }
    public void setSupplierID(Integer supplierID) { this.supplierID = supplierID; }

    public Integer getCategoryID() { return categoryID; }
    public void setCategoryID(Integer categoryID) { this.categoryID = categoryID; }

    public String getQuantityPerUnit() { return quantityPerUnit; }
    public void setQuantityPerUnit(String quantityPerUnit) { this.quantityPerUnit = quantityPerUnit; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public short getUnitsInStock() { return unitsInStock; }
    public void setUnitsInStock(short unitsInStock) { this.unitsInStock = unitsInStock; }

    public short getUnitsOnOrder() { return unitsOnOrder; }
    public void setUnitsOnOrder(short unitsOnOrder) { this.unitsOnOrder = unitsOnOrder; }

    public short getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(short reorderLevel) { this.reorderLevel = reorderLevel; }

    public boolean isDiscontinued() { return discontinued; }
    public void setDiscontinued(boolean discontinued) { this.discontinued = discontinued; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public List<OrderDetails> getOrderDetails() { return orderDetails; }
    public void setOrderDetails(List<OrderDetails> orderDetails) { this.orderDetails = orderDetails; }
}
