package org.course.ecommerce.dto;

import java.math.BigDecimal;

public class ProductDto {

    private int productId;
    private String productName;
    private Integer supplierId;
    private Integer categoryId;
    private String categoryName;
    private String quantityPerUnit;
    private BigDecimal unitPrice;
    private Short unitsInStock;
    private Short unitsOnOrder;
    private Short reorderLevel;
    private boolean discontinued;

    public ProductDto() {}

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Integer getSupplierId() { return supplierId; }
    public void setSupplierId(Integer supplierId) { this.supplierId = supplierId; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getQuantityPerUnit() { return quantityPerUnit; }
    public void setQuantityPerUnit(String quantityPerUnit) { this.quantityPerUnit = quantityPerUnit; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public Short getUnitsInStock() { return unitsInStock; }
    public void setUnitsInStock(Short unitsInStock) { this.unitsInStock = unitsInStock; }

    public Short getUnitsOnOrder() { return unitsOnOrder; }
    public void setUnitsOnOrder(Short unitsOnOrder) { this.unitsOnOrder = unitsOnOrder; }

    public Short getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(Short reorderLevel) { this.reorderLevel = reorderLevel; }

    public boolean isDiscontinued() { return discontinued; }
    public void setDiscontinued(boolean discontinued) { this.discontinued = discontinued; }
}
