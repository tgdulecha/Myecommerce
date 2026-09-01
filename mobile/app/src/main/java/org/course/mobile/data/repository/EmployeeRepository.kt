package org.course.mobile.data.repository

import org.course.mobile.data.network.ApiResult
import org.course.mobile.data.network.EmployeeApi
import org.course.mobile.data.network.dto.EmployeeDto
import org.course.mobile.data.network.safeCall
import org.course.mobile.data.network.safeCallUnit

class EmployeeRepository(private val api: EmployeeApi) {
    suspend fun getAll(): ApiResult<List<EmployeeDto>> = safeCall { api.getAll() }
    suspend fun getById(id: Int): ApiResult<EmployeeDto> = safeCall { api.getById(id) }
    suspend fun create(employee: EmployeeDto): ApiResult<Unit> = safeCallUnit { api.create(employee) }
    suspend fun update(id: Int, employee: EmployeeDto): ApiResult<Unit> = safeCallUnit { api.update(id, employee) }
    suspend fun delete(id: Int): ApiResult<Unit> = safeCallUnit { api.delete(id) }
}
