package org.course.mobile.data.network

import org.course.mobile.data.network.dto.PaymentDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// Mirrors org.course.paymentservice.controller.PaymentController. No update/delete
// server-side - payments are append-only financial records, only create + a
// status transition are supported.
interface PaymentApi {

    @GET("payments")
    suspend fun getAll(@Query("orderId") orderId: Int? = null): Response<List<PaymentDto>>

    @GET("payments/{id}")
    suspend fun getById(@Path("id") id: Int): Response<PaymentDto>

    // Response body IS the created PaymentDto here (unlike ecommerce-service's
    // void-body creates) - status comes back forced to PENDING regardless of what
    // was sent.
    @POST("payments")
    suspend fun create(@Body payment: PaymentDto): Response<PaymentDto>

    @PATCH("payments/{id}/status")
    suspend fun updateStatus(@Path("id") id: Int, @Body status: Map<String, String>): Response<PaymentDto>
}
