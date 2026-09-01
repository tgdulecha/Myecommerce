package org.course.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.course.mobile.data.network.ApiResult
import org.course.mobile.data.network.dto.ProductDto
import org.course.mobile.data.repository.ProductRepository

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _state = MutableStateFlow(ListUiState<ProductDto>())
    val state: StateFlow<ListUiState<ProductDto>> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        _state.value = _state.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            when (val result = repository.getAll()) {
                is ApiResult.Success -> _state.value = ListUiState(items = result.data)
                is ApiResult.Failure -> _state.value = _state.value.copy(isLoading = false, errorMessage = result.message)
            }
        }
    }

    fun save(product: ProductDto, onDone: () -> Unit) {
        viewModelScope.launch {
            val result = if (product.productId == null) {
                repository.create(product)
            } else {
                repository.update(product.productId, product)
            }
            when (result) {
                is ApiResult.Success -> { refresh(); onDone() }
                is ApiResult.Failure -> _state.value = _state.value.copy(errorMessage = result.message)
            }
        }
    }

    fun delete(id: Int) {
        viewModelScope.launch {
            when (val result = repository.delete(id)) {
                is ApiResult.Success -> refresh()
                is ApiResult.Failure -> _state.value = _state.value.copy(errorMessage = result.message)
            }
        }
    }
}
