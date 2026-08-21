package org.course.authservice.service;

import org.course.authservice.dto.AccountDto;
import org.course.authservice.dto.RegisterRequestDto;

public interface AccountService {

    AccountDto register(RegisterRequestDto request);

    AccountDto getByEmail(String email);

    void recordLogin(String email);
}
