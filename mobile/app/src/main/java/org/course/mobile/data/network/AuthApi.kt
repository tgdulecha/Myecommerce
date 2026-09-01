package org.course.mobile.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// Mirrors org.course.authservice.controller.AuthController exactly: register/login
// are public, me requires the bearer token - attached automatically by
// NetworkModule's auth interceptor now, same as every other service's API. Every
// non-2xx response body across all three backend services is a plain string
// (GlobalExceptionHandler), never JSON - see data/network/ApiCall.kt's safeCall.
interface AuthApi {

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<AccountDto>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("me")
    suspend fun me(): Response<AccountDto>
}
