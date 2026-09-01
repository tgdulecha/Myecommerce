package org.course.mobile.data.repository

import kotlinx.coroutines.flow.Flow
import org.course.mobile.data.local.TokenDataStore
import org.course.mobile.data.network.AccountDto
import org.course.mobile.data.network.ApiResult
import org.course.mobile.data.network.AuthApi
import org.course.mobile.data.network.LoginRequest
import org.course.mobile.data.network.RegisterRequest
import org.course.mobile.data.network.safeCall

// Mirrors frontend/src/js/auth.js's exported functions one-for-one: register() then
// auto-login()s with the same credentials, login() persists the token, logout()
// clears it. Screens/ViewModels never touch AuthApi or TokenDataStore directly.
class AuthRepository(
    private val authApi: AuthApi,
    private val tokenDataStore: TokenDataStore,
) {
    val tokenFlow: Flow<String?> = tokenDataStore.tokenFlow

    suspend fun register(request: RegisterRequest): ApiResult<AccountDto> {
        val registered = safeCall { authApi.register(request) }
        if (registered is ApiResult.Failure) return registered

        return login(request.email, request.password)
    }

    suspend fun login(email: String, password: String): ApiResult<AccountDto> {
        return when (val result = safeCall { authApi.login(LoginRequest(email, password)) }) {
            is ApiResult.Success -> {
                tokenDataStore.setToken(result.data.token)
                ApiResult.Success(result.data.account)
            }
            is ApiResult.Failure -> result
        }
    }

    // Null means "no stored session to check" (go straight to sign-in, no network
    // call needed) - distinct from a Failure, which means a stored token turned out
    // to be invalid/expired and was just cleared.
    suspend fun checkSession(): ApiResult<AccountDto>? {
        val token = tokenDataStore.currentToken() ?: return null

        return when (val result = safeCall { authApi.me() }) {
            is ApiResult.Success -> result
            is ApiResult.Failure -> {
                tokenDataStore.clearToken()
                result
            }
        }
    }

    suspend fun logout() {
        tokenDataStore.clearToken()
    }
}
