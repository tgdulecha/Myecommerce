package org.course.mobile.data.repository

import org.course.mobile.data.network.ApiResult
import org.course.mobile.data.network.PaymentApi
import org.course.mobile.data.network.dto.PaymentDto
import org.course.mobile.data.network.dto.PaymentStatus
import org.course.mobile.data.network.safeCall

class PaymentRepository(private val api: PaymentApi) {
    suspend fun getAll(orderId: Int? = null): ApiResult<List<PaymentDto>> = safeCall { api.getAll(orderId) }
    suspend fun getById(id: Int): ApiResult<PaymentDto> = safeCall { api.getById(id) }
    suspend fun create(payment: PaymentDto): ApiResult<PaymentDto> = safeCall { api.create(payment) }
    suspend fun updateStatus(id: Int, status: PaymentStatus): ApiResult<PaymentDto> =
        safeCall { api.updateStatus(id, mapOf("status" to status.name)) }
}
