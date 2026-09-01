package org.course.mobile.data.network

import org.course.mobile.data.network.dto.OrderDetailsDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// Mirrors org.course.ecommerce.controller.OrderDetailsController. The backend has
// two overloaded PUT mappings (bare-collection with ids from the body, and
// .../{orderId}/{productId}) - this client only ever uses the path-variable one,
// matching the convention the web frontend (orderDetailService.js) already
// standardized on.
interface OrderDetailsApi {

    @GET("orderdetails")
    suspend fun getPage(@Query("page") page: Int = 1, @Query("size") size: Int = 15): Response<PageDto<OrderDetailsDto>>

    @GET("orderdetails/{orderId}")
    suspend fun getByOrderId(@Path("orderId") orderId: Int): Response<List<OrderDetailsDto>>

    @GET("orderdetails/{orderId}/{productId}")
    suspend fun getLine(@Path("orderId") orderId: Int, @Path("productId") productId: Int): Response<OrderDetailsDto>

    @POST("orderdetails")
    suspend fun create(@Body line: OrderDetailsDto): Response<Unit>

    @PUT("orderdetails/{orderId}/{productId}")
    suspend fun update(
        @Path("orderId") orderId: Int,
        @Path("productId") productId: Int,
        @Body line: OrderDetailsDto,
    ): Response<Unit>

    @DELETE("orderdetails/{orderId}/{productId}")
    suspend fun delete(@Path("orderId") orderId: Int, @Path("productId") productId: Int): Response<Unit>
}
