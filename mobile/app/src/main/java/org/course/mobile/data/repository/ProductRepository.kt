package org.course.mobile.data.repository

import org.course.mobile.data.network.ApiResult
import org.course.mobile.data.network.ProductApi
import org.course.mobile.data.network.dto.ProductDto
import org.course.mobile.data.network.safeCall
import org.course.mobile.data.network.safeCallUnit

class ProductRepository(private val api: ProductApi) {
    suspend fun getAll(): ApiResult<List<ProductDto>> = safeCall { api.getAll() }
    suspend fun getById(id: Int): ApiResult<ProductDto> = safeCall { api.getById(id) }
    suspend fun create(product: ProductDto): ApiResult<Unit> = safeCallUnit { api.create(product) }
    suspend fun update(id: Int, product: ProductDto): ApiResult<Unit> = safeCallUnit { api.update(id, product) }
    suspend fun delete(id: Int): ApiResult<Unit> = safeCallUnit { api.delete(id) }
}
