package org.course.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.course.mobile.data.network.ApiResult
import org.course.mobile.data.network.dto.CategoryDto
import org.course.mobile.data.repository.CategoryRepository

class CategoryViewModel(private val repository: CategoryRepository) : ViewModel() {

    private val _state = MutableStateFlow(ListUiState<CategoryDto>())
    val state: StateFlow<ListUiState<CategoryDto>> = _state.asStateFlow()

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

    fun save(category: CategoryDto, onDone: () -> Unit) {
        viewModelScope.launch {
            val result = if (category.categoryId == null) {
                repository.create(category)
            } else {
                repository.update(category.categoryId, category)
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
