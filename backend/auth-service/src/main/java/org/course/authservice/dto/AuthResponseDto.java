package org.course.authservice.dto;

public class AuthResponseDto {

    private String token;
    private AccountDto account;

    public AuthResponseDto() {}

    public AuthResponseDto(String token, AccountDto account) {
        this.token = token;
        this.account = account;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public AccountDto getAccount() { return account; }
    public void setAccount(AccountDto account) { this.account = account; }
}
