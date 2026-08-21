package org.course.authservice.service;

import org.course.authservice.dto.AccountDto;
import org.course.authservice.dto.RegisterRequestDto;
import org.course.authservice.entity.Account;
import org.course.authservice.entity.Customer;
import org.course.authservice.exception.NotFoundException;
import org.course.authservice.mapper.AccountMapper;
import org.course.authservice.repository.AccountRepository;
import org.course.authservice.repository.CustomerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountServiceImpl(AccountRepository accountRepository,
                               CustomerRepository customerRepository,
                               PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public AccountDto register(RegisterRequestDto request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty())
            throw new IllegalArgumentException("Email cannot be empty.");

        if (request.getPassword() == null || request.getPassword().length() < 8)
            throw new IllegalArgumentException("Password must be at least 8 characters.");

        if (request.getCompanyName() == null || request.getCompanyName().trim().isEmpty())
            throw new IllegalArgumentException("Company name cannot be empty.");

        if (accountRepository.existsByEmail(request.getEmail()))
            throw new IllegalArgumentException("An account with this email already exists.");

        Customer customer = new Customer();
        customer.setCustomerID(generateCustomerId(request.getCompanyName()));
        customer.setCompanyName(request.getCompanyName());
        customer.setContactName(request.getContactName());
        customer.setContactTitle(request.getContactTitle());
        customer.setAddress(request.getAddress());
        customer.setCity(request.getCity());
        customer.setRegion(request.getRegion());
        customer.setPostalCode(request.getPostalCode());
        customer.setCountry(request.getCountry());
        customer.setPhone(request.getPhone());
        customerRepository.save(customer);

        Account account = new Account();
        account.setEmail(request.getEmail());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setRole("Customer");
        account.setCustomerID(customer.getCustomerID());
        account.setVerified(false);
        account.setCreatedAt(LocalDateTime.now());

        Account saved = accountRepository.save(account);
        return AccountMapper.toDto(saved);
    }

    @Override
    public AccountDto getByEmail(String email) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Account not found."));
        return AccountMapper.toDto(account);
    }

    @Override
    @Transactional
    public void recordLogin(String email) {
        accountRepository.findByEmail(email).ifPresent(account -> {
            account.setLastLogin(LocalDateTime.now());
            accountRepository.save(account);
        });
    }

    // Northwind-style 5-letter customer codes (e.g. "ALFKI"), derived from the company name
    private String generateCustomerId(String companyName) {
        String letters = companyName.toUpperCase().replaceAll("[^A-Z]", "");
        String base = letters.length() >= 5 ? letters.substring(0, 5) : (letters + "XXXXX").substring(0, 5);

        String candidate = base;
        for (int suffix = 1; customerRepository.existsById(candidate); suffix++) {
            if (suffix > 9)
                throw new IllegalArgumentException("Could not generate a unique customer code for this company name.");
            String suffixStr = String.valueOf(suffix);
            candidate = base.substring(0, 5 - suffixStr.length()) + suffixStr;
        }
        return candidate;
    }
}
