package org.course.mobile.ui.viewmodel

// Shared list-screen state shape, reused by every entity ViewModel (Category,
// Product, Employee) that isn't paginated - Order/OrderDetails use PagedUiState
// instead since their list endpoints return a PageDto.
data class ListUiState<T>(
    val items: List<T> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

data class PagedUiState<T>(
    val items: List<T> = emptyList(),
    val page: Int = 1,
    val pageSize: Int = 10,
    val totalPages: Int = 1,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
