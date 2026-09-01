package org.course.mobile.data.repository

import org.course.mobile.data.network.ApiResult
import org.course.mobile.data.network.OrderApi
import org.course.mobile.data.network.PageDto
import org.course.mobile.data.network.dto.OrderDto
import org.course.mobile.data.network.safeCall
import org.course.mobile.data.network.safeCallUnit

class OrderRepository(private val api: OrderApi) {
    suspend fun getPage(page: Int = 1, size: Int = 5): ApiResult<PageDto<OrderDto>> = safeCall { api.getPage(page, size) }
    suspend fun getById(id: Int): ApiResult<OrderDto> = safeCall { api.getById(id) }
    suspend fun create(order: OrderDto): ApiResult<Unit> = safeCallUnit { api.create(order) }
    suspend fun update(id: Int, order: OrderDto): ApiResult<Unit> = safeCallUnit { api.update(id, order) }
    suspend fun delete(id: Int): ApiResult<Unit> = safeCallUnit { api.delete(id) }
}
