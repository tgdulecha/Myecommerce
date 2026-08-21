-- Run manually against a NEW, dedicated database named NorthWind_clone.
-- This is a disposable schema for integration tests only - never point these
-- tests at the real NorthWind database, since the *IT tests perform real
-- inserts (rolled back per-test via @Transactional, but still real writes mid-test).
--
-- Covers every table the current integration tests touch: Auth (Customers,
-- Employees, Accounts) plus Categories, Products, Orders, Order Details for the
-- pre-existing CRUD controllers. FK constraints are intentionally NOT replicated
-- here (unlike the real Northwind) to keep test setup simple - these tests create
-- their own self-contained rows rather than relying on seeded reference data.

CREATE DATABASE NorthWind_clone;
GO

USE NorthWind_clone;
GO

CREATE TABLE Customers (
    CustomerID   NCHAR(5)      NOT NULL PRIMARY KEY,
    CompanyName  NVARCHAR(40)  NOT NULL,
    ContactName  NVARCHAR(30)  NULL,
    ContactTitle NVARCHAR(30)  NULL,
    Address      NVARCHAR(60)  NULL,
    City         NVARCHAR(15)  NULL,
    Region       NVARCHAR(15)  NULL,
    PostalCode   NVARCHAR(10)  NULL,
    Country      NVARCHAR(15)  NULL,
    Phone        NVARCHAR(24)  NULL,
    Fax          NVARCHAR(24)  NULL
);
GO

-- Minimal shape - just enough for the Accounts/Orders FK columns. Add the
-- remaining Northwind Employees columns here if a test later needs them.
CREATE TABLE Employees (
    EmployeeID INT IDENTITY(1,1) PRIMARY KEY,
    LastName   NVARCHAR(20) NOT NULL,
    FirstName  NVARCHAR(10) NOT NULL
);
GO

CREATE TABLE Categories (
    CategoryID   INT IDENTITY(1,1) PRIMARY KEY,
    CategoryName NVARCHAR(15) NOT NULL,
    Description  NTEXT NULL,
    Picture      IMAGE NULL
);
GO

CREATE TABLE Products (
    ProductID       INT IDENTITY(1,1) PRIMARY KEY,
    ProductName     NVARCHAR(40) NOT NULL,
    SupplierID      INT NULL,
    CategoryID      INT NULL,
    QuantityPerUnit NVARCHAR(20) NULL,
    UnitPrice       MONEY NULL,
    UnitsInStock    SMALLINT NULL,
    UnitsOnOrder    SMALLINT NULL,
    ReorderLevel    SMALLINT NULL,
    Discontinued    BIT NOT NULL DEFAULT 0
);
GO

CREATE TABLE Orders (
    OrderID        INT IDENTITY(1,1) PRIMARY KEY,
    CustomerID     NCHAR(5) NULL,
    EmployeeID     INT NULL,
    OrderDate      DATETIME NULL,
    RequiredDate   DATETIME NULL,
    ShippedDate    DATETIME NULL,
    ShipVia        INT NULL,
    Freight        MONEY NULL,
    ShipName       NVARCHAR(40) NULL,
    ShipAddress    NVARCHAR(60) NULL,
    ShipCity       NVARCHAR(15) NULL,
    ShipRegion     NVARCHAR(15) NULL,
    ShipPostalCode NVARCHAR(10) NULL,
    ShipCountry    NVARCHAR(15) NULL
);
GO

-- Table name matches the real Northwind schema, incl. the space - must stay
-- quoted ([Order Details]) everywhere it's referenced.
CREATE TABLE [Order Details] (
    OrderID   INT NOT NULL,
    ProductID INT NOT NULL,
    UnitPrice MONEY NOT NULL,
    Quantity  SMALLINT NOT NULL DEFAULT 1,
    Discount  REAL NOT NULL DEFAULT 0,
    CONSTRAINT PK_OrderDetails PRIMARY KEY (OrderID, ProductID)
);
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
    LastLogin       DATETIME NULL,

    CONSTRAINT FK_Accounts_Customer FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID),
    CONSTRAINT FK_Accounts_Employee FOREIGN KEY (EmployeeID) REFERENCES Employees(EmployeeID),

    CONSTRAINT CHK_Accounts_OneLink CHECK (
        (CustomerID IS NOT NULL AND EmployeeID IS NULL)
        OR
        (CustomerID IS NULL AND EmployeeID IS NOT NULL)
    )
);
GO
