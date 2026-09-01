package org.course.mobile.ui.screens.order

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.course.mobile.data.network.dto.OrderDetailsDto
import org.course.mobile.ui.viewmodel.OrderViewModel

// The order's own fields plus its line items (OrderDetails) together - a composite-
// key line only makes sense in the context of its parent order, so this is the one
// screen for both, per the mobile-app plan.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    viewModel: OrderViewModel,
    orderId: Int,
    onBack: () -> Unit,
    onEditOrder: () -> Unit,
    onAddLine: () -> Unit,
    onEditLine: (OrderDetailsDto) -> Unit,
) {
    val order by viewModel.selectedOrder.collectAsState()
    val lines by viewModel.orderLines.collectAsState()

    LaunchedEffect(orderId) { viewModel.loadOrderDetail(orderId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order #$orderId") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    IconButton(onClick = onEditOrder) { Text("Edit") }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddLine) { Icon(Icons.Default.Add, contentDescription = "Add line item") }
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(12.dp)) {
            order?.let {
                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Customer: ${it.customerId ?: "-"}")
                        Text("Ship to: ${it.shipName ?: "-"}, ${it.shipCity ?: ""} ${it.shipCountry ?: ""}")
                        Text("Freight: ${it.freight ?: "-"}")
                    }
                }
            }

            Text("Line Items", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))

            LazyColumn {
                items(lines, key = { "${it.orderId}-${it.productId}" }) { line ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column {
                                Text(line.productName ?: "Product #${line.productId}", style = MaterialTheme.typography.titleSmall)
                                Text("Qty ${line.quantity ?: "-"} × ${line.unitPrice ?: "-"} (${((line.discount ?: 0f) * 100).toInt()}% off)")
                            }
                            Row {
                                IconButton(onClick = { onEditLine(line) }) { Text("Edit") }
                                IconButton(onClick = { viewModel.deleteLine(line.orderId, line.productId) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete line")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
