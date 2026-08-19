package org.course.ecommerce.service;

import org.course.ecommerce.dto.AccountDto;
import org.course.ecommerce.dto.RegisterRequestDto;

public interface AccountService {

    AccountDto register(RegisterRequestDto request);

    AccountDto getByEmail(String email);

    void recordLogin(String email);
}
