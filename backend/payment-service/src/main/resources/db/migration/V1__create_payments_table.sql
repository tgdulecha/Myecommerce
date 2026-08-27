-- Run manually against the NorthWind database before starting payment-service for
-- the first time (ddl-auto=none - Hibernate will not create this for you). Also
-- mirrored in src/test/resources/sql/northwind_clone_schema.sql for integration tests.
--
-- Unlike Accounts (duplicated from auth-service's tables), Payments is new: this is
-- the first table in the migration owned outright by a single service, with no other
-- service reading or writing it.

USE NorthWind;
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

CREATE INDEX IX_Payments_OrderID ON Payments (OrderID);
GO
