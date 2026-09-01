package org.course.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.course.mobile.data.network.ApiResult
import org.course.mobile.data.network.dto.OrderDetailsDto
import org.course.mobile.data.network.dto.OrderDto
import org.course.mobile.data.repository.OrderDetailsRepository
import org.course.mobile.data.repository.OrderRepository

// Owns both the paginated order list/CRUD and, for whichever order is currently
// open, its line items - an order's detail screen needs both together, and
// OrderDetails only ever makes sense in the context of an order (composite key
// orderId+productId), so this is one ViewModel rather than two disconnected ones.
class OrderViewModel(
    private val orderRepository: OrderRepository,
    private val orderDetailsRepository: OrderDetailsRepository,
) : ViewModel() {

    private val _listState = MutableStateFlow(PagedUiState<OrderDto>())
    val listState: StateFlow<PagedUiState<OrderDto>> = _listState.asStateFlow()

    private val _selectedOrder = MutableStateFlow<OrderDto?>(null)
    val selectedOrder: StateFlow<OrderDto?> = _selectedOrder.asStateFlow()

    private val _orderLines = MutableStateFlow<List<OrderDetailsDto>>(emptyList())
    val orderLines: StateFlow<List<OrderDetailsDto>> = _orderLines.asStateFlow()

    init {
        loadPage(1)
    }

    fun loadPage(page: Int, size: Int = _listState.value.pageSize) {
        _listState.value = _listState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            when (val result = orderRepository.getPage(page, size)) {
                is ApiResult.Success -> _listState.value = PagedUiState(
                    items = result.data.content,
                    page = result.data.page,
                    pageSize = size,
                    totalPages = result.data.totalPages,
                )
                is ApiResult.Failure -> _listState.value = _listState.value.copy(isLoading = false, errorMessage = result.message)
            }
        }
    }

    fun save(order: OrderDto, onDone: () -> Unit) {
        viewModelScope.launch {
            val result = if (order.orderId == null) {
                orderRepository.create(order)
            } else {
                orderRepository.update(order.orderId, order)
            }
            when (result) {
                is ApiResult.Success -> { loadPage(_listState.value.page); onDone() }
                is ApiResult.Failure -> _listState.value = _listState.value.copy(errorMessage = result.message)
            }
        }
    }

    fun delete(id: Int) {
        viewModelScope.launch {
            when (val result = orderRepository.delete(id)) {
                is ApiResult.Success -> loadPage(_listState.value.page)
                is ApiResult.Failure -> _listState.value = _listState.value.copy(errorMessage = result.message)
            }
        }
    }

    fun loadOrderDetail(orderId: Int) {
        viewModelScope.launch {
            when (val result = orderRepository.getById(orderId)) {
                is ApiResult.Success -> _selectedOrder.value = result.data
                is ApiResult.Failure -> _listState.value = _listState.value.copy(errorMessage = result.message)
            }
            when (val result = orderDetailsRepository.getByOrderId(orderId)) {
                is ApiResult.Success -> _orderLines.value = result.data
                is ApiResult.Failure -> _listState.value = _listState.value.copy(errorMessage = result.message)
            }
        }
    }

    fun saveLine(line: OrderDetailsDto, isNew: Boolean, onDone: () -> Unit) {
        viewModelScope.launch {
            val result = if (isNew) {
                orderDetailsRepository.create(line)
            } else {
                orderDetailsRepository.update(line.orderId, line.productId, line)
            }
            when (result) {
                is ApiResult.Success -> { loadOrderDetail(line.orderId); onDone() }
                is ApiResult.Failure -> _listState.value = _listState.value.copy(errorMessage = result.message)
            }
        }
    }

    fun deleteLine(orderId: Int, productId: Int) {
        viewModelScope.launch {
            when (val result = orderDetailsRepository.delete(orderId, productId)) {
                is ApiResult.Success -> loadOrderDetail(orderId)
                is ApiResult.Failure -> _listState.value = _listState.value.copy(errorMessage = result.message)
            }
        }
    }
}
