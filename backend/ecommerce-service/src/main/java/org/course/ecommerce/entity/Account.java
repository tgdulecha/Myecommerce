package org.course.ecommerce.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Duplicated from auth-service: Phase 1 of the SOA migration keeps one shared
 * database, and every service still authenticates its own requests by loading the
 * caller's Account row rather than calling auth-service back on each request.
 * auth-service remains the only writer - this copy is read-only in practice.
 */
@Entity
@Table(name = "Accounts", schema = "dbo")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AccountID")
    private int accountID;

    @Column(name = "Email", nullable = false, unique = true)
    private String email;

    @Column(name = "PasswordHash", nullable = false)
    private String password;

    @Column(name = "Role", nullable = false)
    private String role;

    @Column(name = "CustomerID", length = 5)
    private String customerID;

    @Column(name = "EmployeeID")
    private Integer employeeID;

    @Column(name = "IsVerified", nullable = false)
    private boolean verified;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "LastLogin")
    private LocalDateTime lastLogin;

    public Account() {}

    public int getAccountID() { return accountID; }
    public void setAccountID(int accountID) { this.accountID = accountID; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getCustomerID() { return customerID; }
    public void setCustomerID(String customerID) { this.customerID = customerID; }

    public Integer getEmployeeID() { return employeeID; }
    public void setEmployeeID(Integer employeeID) { this.employeeID = employeeID; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
}
