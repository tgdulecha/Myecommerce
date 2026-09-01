package org.course.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.course.mobile.data.network.ApiResult
import org.course.mobile.data.network.dto.OrderDetailsDto
import org.course.mobile.data.repository.OrderDetailsRepository

// The standalone top-level "all order lines across every order" view - editing a
// specific line happens via OrderViewModel from within its parent order's screen,
// this is just the flat browsable list.
class OrderDetailsListViewModel(private val repository: OrderDetailsRepository) : ViewModel() {

    private val _state = MutableStateFlow(PagedUiState<OrderDetailsDto>())
    val state: StateFlow<PagedUiState<OrderDetailsDto>> = _state.asStateFlow()

    init {
        loadPage(1)
    }

    fun loadPage(page: Int) {
        _state.value = _state.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            when (val result = repository.getPage(page)) {
                is ApiResult.Success -> _state.value = PagedUiState(
                    items = result.data.content,
                    page = result.data.page,
                    totalPages = result.data.totalPages,
                )
                is ApiResult.Failure -> _state.value = _state.value.copy(isLoading = false, errorMessage = result.message)
            }
        }
    }
}
