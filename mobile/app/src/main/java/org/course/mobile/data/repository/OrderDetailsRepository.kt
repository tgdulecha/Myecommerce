package org.course.mobile.data.repository

import org.course.mobile.data.network.ApiResult
import org.course.mobile.data.network.OrderDetailsApi
import org.course.mobile.data.network.PageDto
import org.course.mobile.data.network.dto.OrderDetailsDto
import org.course.mobile.data.network.safeCall
import org.course.mobile.data.network.safeCallUnit

class OrderDetailsRepository(private val api: OrderDetailsApi) {
    suspend fun getPage(page: Int = 1, size: Int = 15): ApiResult<PageDto<OrderDetailsDto>> = safeCall { api.getPage(page, size) }
    suspend fun getByOrderId(orderId: Int): ApiResult<List<OrderDetailsDto>> = safeCall { api.getByOrderId(orderId) }
    suspend fun getLine(orderId: Int, productId: Int): ApiResult<OrderDetailsDto> = safeCall { api.getLine(orderId, productId) }
    suspend fun create(line: OrderDetailsDto): ApiResult<Unit> = safeCallUnit { api.create(line) }
    suspend fun update(orderId: Int, productId: Int, line: OrderDetailsDto): ApiResult<Unit> =
        safeCallUnit { api.update(orderId, productId, line) }
    suspend fun delete(orderId: Int, productId: Int): ApiResult<Unit> = safeCallUnit { api.delete(orderId, productId) }
}
