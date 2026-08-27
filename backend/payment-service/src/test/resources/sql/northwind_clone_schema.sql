-- Run manually against a NEW, dedicated database named NorthWind_clone.
-- This is a disposable schema for integration tests only - never point these
-- tests at the real NorthWind database, since the *IT tests perform real
-- inserts (rolled back per-test via @Transactional, but still real writes mid-test).
--
-- Covers every table payment-service's integration tests touch: Accounts (for JWT
-- auth, duplicated from auth-service's clone) plus Payments, the table this service
-- owns. FK constraints to Orders/Customers are intentionally NOT replicated here -
-- payment-service never joins against them, only stores the OrderID/CustomerEmail
-- values it was given.

CREATE DATABASE NorthWind_clone;
GO

USE NorthWind_clone;
GO

CREATE TABLE Accounts (
    AccountID       INT IDENTITY(1,1) PRIMARY KEY,
    Email           NVARCHAR(255) NOT NULL UNIQUE,
    PasswordHash    NVARCHAR(255) NOT NULL,
    Role            NVARCHAR(20) NOT NULL CHECK (Role IN ('Customer', 'Employee', 'Admin')),
    CustomerID      NCHAR(5) NULL,
    EmployeeID      INT NULL,
    IsVerified      BIT NOT NULL DEFAULT 0,
    CreatedAt       DATETIME NOT NULL DEFAULT GETDATE(),
    LastLogin       DATETIME NULL
);
GO

CREATE TABLE Payments (
    PaymentID       INT IDENTITY(1,1) PRIMARY KEY,
    OrderID         INT NOT NULL,
    CustomerEmail   NVARCHAR(255) NOT NULL,
    Amount          DECIMAL(19,2) NOT NULL,
    Method          NVARCHAR(30) NOT NULL,
    Status          NVARCHAR(20) NOT NULL CHECK (Status IN ('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED')),
    TransactionDate DATETIME NULL,
    CreatedAt       DATETIME NOT NULL DEFAULT GETDATE()
);
GO
