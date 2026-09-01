package org.course.mobile.ui.screens.orderdetails

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.course.mobile.ui.viewmodel.OrderDetailsListViewModel

// Read-only flat browse of every order line across every order - editing a
// specific line happens from its parent order's detail screen (composite key
// orderId+productId only makes sense in that context).
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsListScreen(
    viewModel: OrderDetailsListViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Order Lines") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                },
            )
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                state.errorMessage != null -> Text(
                    state.errorMessage.orEmpty(),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                )
                else -> Column(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(modifier = Modifier.weight(1f).padding(12.dp)) {
                        items(state.items, key = { "${it.orderId}-${it.productId}" }) { line ->
                            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Order #${line.orderId} · ${line.productName ?: "Product #${line.productId}"}")
                                    Text("Qty ${line.quantity ?: "-"} × ${line.unitPrice ?: "-"}", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        OutlinedButton(onClick = { viewModel.loadPage(state.page - 1) }, enabled = state.page > 1) {
                            Text("Previous")
                        }
                        Text("Page ${state.page} / ${state.totalPages}", modifier = Modifier.padding(top = 8.dp))
                        OutlinedButton(onClick = { viewModel.loadPage(state.page + 1) }, enabled = state.page < state.totalPages) {
                            Text("Next")
                        }
                    }
                }
            }
        }
    }
}
