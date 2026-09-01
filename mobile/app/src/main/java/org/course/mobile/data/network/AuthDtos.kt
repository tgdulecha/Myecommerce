package org.course.mobile.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Mirrors org.course.authservice.dto.RegisterRequestDto exactly - field-for-field,
// including which ones are actually required server-side (email/password/companyName;
// see AccountServiceImpl.register()'s manual checks - the rest are optional Customer
// profile fields).
@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    @SerialName("companyName") val companyName: String,
    @SerialName("contactName") val contactName: String? = null,
    @SerialName("contactTitle") val contactTitle: String? = null,
    val address: String? = null,
    val city: String? = null,
    val region: String? = null,
    @SerialName("postalCode") val postalCode: String? = null,
    val country: String? = null,
    val phone: String? = null,
)

// Mirrors org.course.authservice.dto.LoginRequestDto.
@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
)

// Mirrors org.course.authservice.dto.AuthResponseDto.
@Serializable
data class AuthResponse(
    val token: String,
    val account: AccountDto,
)

// Mirrors org.course.authservice.dto.AccountDto (via AccountMapper.toDto). Jackson's
// default property naming keeps the Java field name as-is (getAccountID() ->
// "accountID"), so @SerialName pins the wire format explicitly rather than relying on
// Kotlin's more idiomatic accountId matching by coincidence.
@Serializable
data class AccountDto(
    @SerialName("accountID") val accountId: Int,
    val email: String,
    val role: String,
    @SerialName("customerID") val customerId: String? = null,
    @SerialName("employeeID") val employeeId: Int? = null,
    val verified: Boolean,
    val createdAt: String? = null,
    val lastLogin: String? = null,
)
