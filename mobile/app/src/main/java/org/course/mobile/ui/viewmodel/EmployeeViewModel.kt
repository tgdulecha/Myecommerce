package org.course.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.course.mobile.data.network.ApiResult
import org.course.mobile.data.network.dto.EmployeeDto
import org.course.mobile.data.repository.EmployeeRepository

class EmployeeViewModel(private val repository: EmployeeRepository) : ViewModel() {

    private val _state = MutableStateFlow(ListUiState<EmployeeDto>())
    val state: StateFlow<ListUiState<EmployeeDto>> = _state.asStateFlow()

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

    fun save(employee: EmployeeDto, onDone: () -> Unit) {
        viewModelScope.launch {
            val result = if (employee.employeeId == null) {
                repository.create(employee)
            } else {
                repository.update(employee.employeeId, employee)
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
