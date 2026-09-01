package org.course.mobile.data.repository

import org.course.mobile.data.network.ApiResult
import org.course.mobile.data.network.CategoryApi
import org.course.mobile.data.network.dto.CategoryDto
import org.course.mobile.data.network.safeCall
import org.course.mobile.data.network.safeCallUnit

class CategoryRepository(private val api: CategoryApi) {
    suspend fun getAll(): ApiResult<List<CategoryDto>> = safeCall { api.getAll() }
    suspend fun getById(id: Int): ApiResult<CategoryDto> = safeCall { api.getById(id) }
    suspend fun create(category: CategoryDto): ApiResult<Unit> = safeCallUnit { api.create(category) }
    suspend fun update(id: Int, category: CategoryDto): ApiResult<Unit> = safeCallUnit { api.update(id, category) }
    suspend fun delete(id: Int): ApiResult<Unit> = safeCallUnit { api.delete(id) }
}
