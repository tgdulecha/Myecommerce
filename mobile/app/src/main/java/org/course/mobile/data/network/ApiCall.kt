package org.course.mobile.data.network

import retrofit2.Response
import java.io.IOException

// Every non-2xx response body across all three backend services is a plain string
// message (GlobalExceptionHandler pattern shared by auth-service, ecommerce-service,
// payment-service), never JSON - so failures carry a String, never a structured
// error type.
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Failure(val message: String) : ApiResult<Nothing>()
}

// Shared by every repository (Auth, Product, Category, Employee, Order,
// OrderDetails, Payment) - reads a Retrofit Response<T>, treats a non-2xx body as
// the plain-text error message every service actually sends, and turns network
// failures into a user-facing message rather than letting an exception propagate.
suspend fun <T> safeCall(block: suspend () -> Response<T>): ApiResult<T> {
    return try {
        val response = block()
        val body = response.body()
        if (response.isSuccessful && body != null) {
            ApiResult.Success(body)
        } else {
            val message = response.errorBody()?.string()?.takeIf { it.isNotBlank() }
                ?: "Request failed (${response.code()})"
            ApiResult.Failure(message)
        }
    } catch (e: IOException) {
        ApiResult.Failure("Network error - check your connection and try again.")
    } catch (e: Exception) {
        ApiResult.Failure(e.message ?: "Unexpected error")
    }
}

// For endpoints that return 201 Created with no body (every ecommerce-service
// create endpoint) - success just means "didn't throw / wasn't an error status".
suspend fun safeCallUnit(block: suspend () -> Response<Unit>): ApiResult<Unit> {
    return try {
        val response = block()
        if (response.isSuccessful) {
            ApiResult.Success(Unit)
        } else {
            val message = response.errorBody()?.string()?.takeIf { it.isNotBlank() }
                ?: "Request failed (${response.code()})"
            ApiResult.Failure(message)
        }
    } catch (e: IOException) {
        ApiResult.Failure("Network error - check your connection and try again.")
    } catch (e: Exception) {
        ApiResult.Failure(e.message ?: "Unexpected error")
    }
}
