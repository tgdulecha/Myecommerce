package org.course.ecommerce.mapper;

import org.course.ecommerce.dto.AccountDto;
import org.course.ecommerce.entity.Account;

public final class AccountMapper {

    private AccountMapper() {}

    public static AccountDto toDto(Account entity) {
        if (entity == null) return null;

        AccountDto dto = new AccountDto();
        dto.setAccountID(entity.getAccountID());
        dto.setEmail(entity.getEmail());
        dto.setRole(entity.getRole());
        dto.setCustomerID(entity.getCustomerID());
        dto.setEmployeeID(entity.getEmployeeID());
        dto.setVerified(entity.isVerified());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setLastLogin(entity.getLastLogin());
        return dto;
    }
}
