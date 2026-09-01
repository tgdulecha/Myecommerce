package org.course.mobile.ui.screens.order

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.course.mobile.data.network.dto.OrderDto
import org.course.mobile.ui.viewmodel.OrderViewModel

// The app's landing screen post-login - mirrors the web frontend's Order Management
// page (blue top bar, "List of Orders" panel with Prev/Page/Next + page-size
// paging, a prominent "Add order" action). The web version splits list/detail into
// two panes side by side; on a phone there's no room for that, so tapping an order
// navigates to its detail screen instead - the standard mobile adaptation of a
// master-detail layout. Every other feature area (Categories, Products, Employees,
// All Order Lines, Payments) plus Logout live behind the top bar's menu, replacing
// what used to be a separate dashboard screen.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListScreen(
    viewModel: OrderViewModel,
    onCreate: () -> Unit,
    onOpen: (OrderDto) -> Unit,
    onNavigateCategories: () -> Unit,
    onNavigateProducts: () -> Unit,
    onNavigateEmployees: () -> Unit,
    onNavigateOrderDetails: () -> Unit,
    onNavigatePayments: () -> Unit,
    onLogout: () -> Unit,
) {
    val state by viewModel.listState.collectAsState()
    var menuExpanded by remember { mutableStateOf(false) }
    var sizeMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventory Management System") },
                navigationIcon = {
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                        DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                            DropdownMenuItem(text = { Text("Categories") }, onClick = { menuExpanded = false; onNavigateCategories() })
                            DropdownMenuItem(text = { Text("Products") }, onClick = { menuExpanded = false; onNavigateProducts() })
                            DropdownMenuItem(text = { Text("Employees") }, onClick = { menuExpanded = false; onNavigateEmployees() })
                            DropdownMenuItem(text = { Text("All Order Lines") }, onClick = { menuExpanded = false; onNavigateOrderDetails() })
                            DropdownMenuItem(text = { Text("Payments") }, onClick = { menuExpanded = false; onNavigatePayments() })
                            DropdownMenuItem(text = { Text("Log Out") }, onClick = { menuExpanded = false; onLogout() })
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2E4E8F),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                ),
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(12.dp)) {
            Text("Order Management", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
            Text("List of Orders", style = MaterialTheme.typography.titleSmall)

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = { viewModel.loadPage(state.page - 1) }, enabled = state.page > 1) { Text("Prev") }
                    Text("Page ${state.page} / ${state.totalPages}")
                    TextButton(onClick = { viewModel.loadPage(state.page + 1) }, enabled = state.page < state.totalPages) { Text("Next") }
                }
                Box {
                    TextButton(onClick = { sizeMenuExpanded = true }) { Text("${state.pageSize} / page") }
                    DropdownMenu(expanded = sizeMenuExpanded, onDismissRequest = { sizeMenuExpanded = false }) {
                        listOf(5, 10, 20).forEach { size ->
                            DropdownMenuItem(text = { Text("$size") }, onClick = { sizeMenuExpanded = false; viewModel.loadPage(1, size) })
                        }
                    }
                }
            }

            Button(onClick = onCreate, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Text("Add order")
            }

            when {
                state.isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                state.errorMessage != null -> Text(state.errorMessage.orEmpty(), color = MaterialTheme.colorScheme.error)
                state.items.isEmpty() -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No order selected", style = MaterialTheme.typography.titleMedium)
                }
                else -> LazyColumn(modifier = Modifier.weight(1f)) {
                    items(state.items, key = { it.orderId ?: it.hashCode() }) { order ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp).clickable { onOpen(order) },
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Column {
                                    Text("Order #${order.orderId}", style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        "${order.shipCity.orEmpty()} ${order.shipCountry.orEmpty()} · Freight: ${order.freight ?: "-"}",
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                                IconButton(onClick = { order.orderId?.let(viewModel::delete) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
