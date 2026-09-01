package org.course.mobile.data.network

import org.course.mobile.data.network.dto.ProductDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.Path

// Mirrors org.course.ecommerce.controller.ProductController exactly.
interface ProductApi {

    @GET("products")
    suspend fun getAll(): Response<List<ProductDto>>

    @GET("products/{id}")
    suspend fun getById(@Path("id") id: Int): Response<ProductDto>

    @POST("products")
    suspend fun create(@Body product: ProductDto): Response<Unit>

    @PUT("products/{id}")
    suspend fun update(@Path("id") id: Int, @Body product: ProductDto): Response<Unit>

    @DELETE("products/{id}")
    suspend fun delete(@Path("id") id: Int): Response<Unit>
}
