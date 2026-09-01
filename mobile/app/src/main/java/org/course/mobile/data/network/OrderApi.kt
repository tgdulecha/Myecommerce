package org.course.mobile.data.network

import org.course.mobile.data.network.dto.OrderDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// Mirrors org.course.ecommerce.controller.OrderController exactly. /all
// (unpaginated) exists server-side but isn't used by the mobile app - the paginated
// list is what the UI is built around.
interface OrderApi {

    @GET("orders")
    suspend fun getPage(@Query("page") page: Int = 1, @Query("size") size: Int = 5): Response<PageDto<OrderDto>>

    @GET("orders/{id}")
    suspend fun getById(@Path("id") id: Int): Response<OrderDto>

    @POST("orders")
    suspend fun create(@Body order: OrderDto): Response<Unit>

    @PUT("orders/{id}")
    suspend fun update(@Path("id") id: Int, @Body order: OrderDto): Response<Unit>

    @DELETE("orders/{id}")
    suspend fun delete(@Path("id") id: Int): Response<Unit>
}
