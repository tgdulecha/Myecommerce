package org.course.authservice.mapper;

import org.course.authservice.dto.AccountDto;
import org.course.authservice.entity.Account;

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
