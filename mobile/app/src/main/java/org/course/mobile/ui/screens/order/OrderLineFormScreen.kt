package org.course.mobile.ui.screens.order

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.course.mobile.data.network.dto.OrderDetailsDto

// productId is fixed once a line exists (it's half of the composite primary key)
// - only editable when adding a brand-new line.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderLineFormScreen(
    orderId: Int,
    existing: OrderDetailsDto?,
    onBack: () -> Unit,
    onSave: (OrderDetailsDto, isNew: Boolean) -> Unit,
) {
    var productId by remember { mutableStateOf(existing?.productId?.toString().orEmpty()) }
    var unitPrice by remember { mutableStateOf(existing?.unitPrice?.toString().orEmpty()) }
    var quantity by remember { mutableStateOf(existing?.quantity?.toString().orEmpty()) }
    var discount by remember { mutableStateOf(existing?.discount?.toString() ?: "0") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (existing == null) "Add Line Item" else "Edit Line Item") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                productId, { productId = it }, label = { Text("Product ID") },
                enabled = existing == null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                unitPrice, { unitPrice = it }, label = { Text("Unit Price") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                quantity, { quantity = it }, label = { Text("Quantity") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                discount, { discount = it }, label = { Text("Discount (0.0-1.0)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
            )

            Button(
                onClick = {
                    val pid = productId.toIntOrNull() ?: return@Button
                    onSave(
                        OrderDetailsDto(
                            orderId = orderId,
                            productId = pid,
                            unitPrice = unitPrice.toDoubleOrNull(),
                            quantity = quantity.toShortOrNull(),
                            discount = discount.toFloatOrNull() ?: 0f,
                        ),
                        existing == null,
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Save")
            }
        }
    }
}
