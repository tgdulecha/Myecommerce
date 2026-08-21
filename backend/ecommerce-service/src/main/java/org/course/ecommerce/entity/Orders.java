package org.course.ecommerce.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Orders", schema = "dbo")
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OrderID")
    private int orderId;

    @Column(name = "CustomerID")
    private String customerId;

    @Column(name = "EmployeeID")
    private int employeeId;

    @Column(name = "OrderDate")
    private LocalDate orderDate;

    @Column(name = "RequiredDate")
    private LocalDate requiredDate;

    @Column(name = "ShippedDate")
    private LocalDate shippedDate;

    @Column(name = "ShipVia")
    private int shipVia;

    @Column(name = "Freight")
    private BigDecimal freight;

    @Column(name = "ShipName")
    private String shipName;

    @Column(name = "ShipAddress")
    private String shipAddress;

    @Column(name = "ShipCity")
    private String shipCity;

    @Column(name = "ShipRegion")
    private String shipRegion;

    @Column(name = "ShipPostalCode")
    private String shipPostalCode;

    @Column(name = "ShipCountry")
    private String shipCountry;

    // Read-only navigation shadowing employeeId - writes still go through setEmployeeId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EmployeeID", insertable = false, updatable = false)
    private Employee employee;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderDetails> orderDetails;

    public Orders() {}

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }

    public LocalDate getRequiredDate() { return requiredDate; }
    public void setRequiredDate(LocalDate requiredDate) { this.requiredDate = requiredDate; }

    public LocalDate getShippedDate() { return shippedDate; }
    public void setShippedDate(LocalDate shippedDate) { this.shippedDate = shippedDate; }

    public int getShipVia() { return shipVia; }
    public void setShipVia(int shipVia) { this.shipVia = shipVia; }

    public BigDecimal getFreight() { return freight; }
    public void setFreight(BigDecimal freight) { this.freight = freight; }

    public String getShipName() { return shipName; }
    public void setShipName(String shipName) { this.shipName = shipName; }

    public String getShipAddress() { return shipAddress; }
    public void setShipAddress(String shipAddress) { this.shipAddress = shipAddress; }

    public String getShipCity() { return shipCity; }
    public void setShipCity(String shipCity) { this.shipCity = shipCity; }

    public String getShipRegion() { return shipRegion; }
    public void setShipRegion(String shipRegion) { this.shipRegion = shipRegion; }

    public String getShipPostalCode() { return shipPostalCode; }
    public void setShipPostalCode(String shipPostalCode) { this.shipPostalCode = shipPostalCode; }

    public String getShipCountry() { return shipCountry; }
    public void setShipCountry(String shipCountry) { this.shipCountry = shipCountry; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public List<OrderDetails> getOrderDetails() { return orderDetails; }
    public void setOrderDetails(List<OrderDetails> orderDetails) { this.orderDetails = orderDetails; }
}
