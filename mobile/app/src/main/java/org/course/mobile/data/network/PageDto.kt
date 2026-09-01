package org.course.mobile.data.network

import kotlinx.serialization.Serializable

// Mirrors the backend's generic PageDto<T> envelope used by Order and OrderDetails
// list endpoints ({ content, page, pageSize, totalElements, totalPages }).
@Serializable
data class PageDto<T>(
    val content: List<T>,
    val page: Int,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Int,
)
