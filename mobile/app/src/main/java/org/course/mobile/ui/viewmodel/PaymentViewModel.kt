package org.course.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.course.mobile.data.network.ApiResult
import org.course.mobile.data.network.dto.PaymentDto
import org.course.mobile.data.network.dto.PaymentStatus
import org.course.mobile.data.repository.PaymentRepository

class PaymentViewModel(private val repository: PaymentRepository) : ViewModel() {

    private val _state = MutableStateFlow(ListUiState<PaymentDto>())
    val state: StateFlow<ListUiState<PaymentDto>> = _state.asStateFlow()

    private var currentOrderFilter: Int? = null

    init {
        refresh()
    }

    fun refresh(orderId: Int? = currentOrderFilter) {
        currentOrderFilter = orderId
        _state.value = _state.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            when (val result = repository.getAll(orderId)) {
                is ApiResult.Success -> _state.value = ListUiState(items = result.data)
                is ApiResult.Failure -> _state.value = _state.value.copy(isLoading = false, errorMessage = result.message)
            }
        }
    }

    fun create(payment: PaymentDto, onDone: () -> Unit) {
        viewModelScope.launch {
            when (val result = repository.create(payment)) {
                is ApiResult.Success -> { refresh(); onDone() }
                is ApiResult.Failure -> _state.value = _state.value.copy(errorMessage = result.message)
            }
        }
    }

    fun updateStatus(id: Int, status: PaymentStatus) {
        viewModelScope.launch {
            when (val result = repository.updateStatus(id, status)) {
                is ApiResult.Success -> refresh()
                is ApiResult.Failure -> _state.value = _state.value.copy(errorMessage = result.message)
            }
        }
    }
}
