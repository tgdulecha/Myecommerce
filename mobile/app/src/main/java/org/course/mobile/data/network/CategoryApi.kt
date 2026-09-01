package org.course.mobile.data.network

import org.course.mobile.data.network.dto.CategoryDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.Path

// Mirrors org.course.ecommerce.controller.CategoryController exactly - every
// endpoint requires the bearer token, attached automatically by NetworkModule's
// auth interceptor.
interface CategoryApi {

    @GET("category")
    suspend fun getAll(): Response<List<CategoryDto>>

    @GET("category/{id}")
    suspend fun getById(@Path("id") id: Int): Response<CategoryDto>

    // 201 Created with a Location header, no body.
    @POST("category")
    suspend fun create(@Body category: CategoryDto): Response<Unit>

    // Body's categoryID must match the path id server-side, else 400.
    @PUT("category/{id}")
    suspend fun update(@Path("id") id: Int, @Body category: CategoryDto): Response<Unit>

    @DELETE("category/{id}")
    suspend fun delete(@Path("id") id: Int): Response<Unit>
}
