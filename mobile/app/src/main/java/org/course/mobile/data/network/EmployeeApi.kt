package org.course.mobile.data.network

import org.course.mobile.data.network.dto.EmployeeDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.Path

// Mirrors org.course.ecommerce.controller.EmployeeController exactly.
interface EmployeeApi {

    @GET("employee")
    suspend fun getAll(): Response<List<EmployeeDto>>

    @GET("employee/{id}")
    suspend fun getById(@Path("id") id: Int): Response<EmployeeDto>

    @POST("employee")
    suspend fun create(@Body employee: EmployeeDto): Response<Unit>

    @PUT("employee/{id}")
    suspend fun update(@Path("id") id: Int, @Body employee: EmployeeDto): Response<Unit>

    @DELETE("employee/{id}")
    suspend fun delete(@Path("id") id: Int): Response<Unit>
}
