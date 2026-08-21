package org.course.ecommerce.service;

import org.course.ecommerce.dto.AccountDto;
import org.course.ecommerce.dto.RegisterRequestDto;
import org.course.ecommerce.entity.Account;
import org.course.ecommerce.entity.Customer;
import org.course.ecommerce.exception.NotFoundException;
import org.course.ecommerce.repository.AccountRepository;
import org.course.ecommerce.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AccountServiceImpl accountService;

    private RegisterRequestDto validRequest() {
        RegisterRequestDto request = new RegisterRequestDto();
        request.setEmail("jane@example.com");
        request.setPassword("supersecret1");
        request.setCompanyName("Acme Corp");
        return request;
    }

    private void setUp() {
        accountService = new AccountServiceImpl(accountRepository, customerRepository, passwordEncoder);
    }

    @Test
    void registerRejectsBlankEmail() {
        setUp();
        RegisterRequestDto request = validRequest();
        request.setEmail("  ");

        assertThatThrownBy(() -> accountService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email");

        verifyNoInteractions(accountRepository, customerRepository, passwordEncoder);
    }

    @Test
    void registerRejectsShortPassword() {
        setUp();
        RegisterRequestDto request = validRequest();
        request.setPassword("short");

        assertThatThrownBy(() -> accountService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password");
    }

    @Test
    void registerRejectsBlankCompanyName() {
        setUp();
        RegisterRequestDto request = validRequest();
        request.setCompanyName("   ");

        assertThatThrownBy(() -> accountService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Company name");
    }

    @Test
    void registerRejectsDuplicateEmail() {
        setUp();
        when(accountRepository.existsByEmail("jane@example.com")).thenReturn(true);

        assertThatThrownBy(() -> accountService.register(validRequest()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        verify(customerRepository, never()).save(any());
    }

    @Test
    void registerCreatesLinkedCustomerAndAccount() {
        setUp();
        when(accountRepository.existsByEmail(anyString())).thenReturn(false);
        when(customerRepository.existsById(anyString())).thenReturn(false);
        when(passwordEncoder.encode("supersecret1")).thenReturn("hashed-password");
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        AccountDto result = accountService.register(validRequest());

        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(customerCaptor.capture());
        Customer savedCustomer = customerCaptor.getValue();
        assertThat(savedCustomer.getCustomerID()).isEqualTo("ACMEC");
        assertThat(savedCustomer.getCompanyName()).isEqualTo("Acme Corp");

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(accountCaptor.capture());
        Account savedAccount = accountCaptor.getValue();
        assertThat(savedAccount.getEmail()).isEqualTo("jane@example.com");
        assertThat(savedAccount.getPassword()).isEqualTo("hashed-password");
        assertThat(savedAccount.getRole()).isEqualTo("Customer");
        assertThat(savedAccount.getCustomerID()).isEqualTo("ACMEC");
        assertThat(savedAccount.getEmployeeID()).isNull();
        assertThat(savedAccount.isVerified()).isFalse();

        assertThat(result.getEmail()).isEqualTo("jane@example.com");
        assertThat(result.getCustomerID()).isEqualTo("ACMEC");
    }

    @Test
    void registerFallsBackToSuffixedCodeOnCollision() {
        setUp();
        when(accountRepository.existsByEmail(anyString())).thenReturn(false);
        when(customerRepository.existsById("ACMEC")).thenReturn(true);
        when(customerRepository.existsById("ACME1")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed-password");
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        AccountDto result = accountService.register(validRequest());

        assertThat(result.getCustomerID()).isEqualTo("ACME1");
    }

    @Test
    void registerPadsShortCompanyNamesWithX() {
        setUp();
        when(accountRepository.existsByEmail(anyString())).thenReturn(false);
        when(customerRepository.existsById(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed-password");
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        RegisterRequestDto request = validRequest();
        request.setCompanyName("Go");

        AccountDto result = accountService.register(request);

        assertThat(result.getCustomerID()).isEqualTo("GOXXX");
    }

    @Test
    void getByEmailThrowsWhenAccountMissing() {
        setUp();
        when(accountRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.getByEmail("missing@example.com"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void recordLoginStampsLastLoginWhenAccountExists() {
        setUp();
        Account account = new Account();
        account.setEmail("jane@example.com");
        when(accountRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(account));

        accountService.recordLogin("jane@example.com");

        assertThat(account.getLastLogin()).isNotNull();
        verify(accountRepository).save(account);
    }

    @Test
    void recordLoginIsNoOpWhenAccountMissing() {
        setUp();
        when(accountRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        accountService.recordLogin("missing@example.com");

        verify(accountRepository, never()).save(any());
    }
}
