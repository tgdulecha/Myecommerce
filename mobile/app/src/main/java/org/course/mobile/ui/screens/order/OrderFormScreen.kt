package org.course.mobile.ui.screens.order

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import org.course.mobile.data.network.dto.OrderDto

// Dates are entered as plain ISO strings (yyyy-MM-dd) rather than a date-picker
// widget - keeps the form consistent with how OrderDto already treats dates as raw
// strings (see the DTO's comment), and avoids pulling in a date-picker dependency
// for a full-CRUD-everywhere v1.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderFormScreen(
    existing: OrderDto?,
    onBack: () -> Unit,
    onSave: (OrderDto) -> Unit,
) {
    var customerId by remember { mutableStateOf(existing?.customerId.orEmpty()) }
    var employeeId by remember { mutableStateOf(existing?.employeeId?.toString().orEmpty()) }
    var orderDate by remember { mutableStateOf(existing?.orderDate.orEmpty()) }
    var requiredDate by remember { mutableStateOf(existing?.requiredDate.orEmpty()) }
    var shippedDate by remember { mutableStateOf(existing?.shippedDate.orEmpty()) }
    var freight by remember { mutableStateOf(existing?.freight?.toString().orEmpty()) }
    var shipName by remember { mutableStateOf(existing?.shipName.orEmpty()) }
    var shipAddress by remember { mutableStateOf(existing?.shipAddress.orEmpty()) }
    var shipCity by remember { mutableStateOf(existing?.shipCity.orEmpty()) }
    var shipRegion by remember { mutableStateOf(existing?.shipRegion.orEmpty()) }
    var shipPostalCode by remember { mutableStateOf(existing?.shipPostalCode.orEmpty()) }
    var shipCountry by remember { mutableStateOf(existing?.shipCountry.orEmpty()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (existing == null) "New Order" else "Edit Order") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(customerId, { customerId = it }, label = { Text("Customer ID") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(
                employeeId, { employeeId = it }, label = { Text("Employee ID") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(orderDate, { orderDate = it }, label = { Text("Order Date (yyyy-MM-dd)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(requiredDate, { requiredDate = it }, label = { Text("Required Date (yyyy-MM-dd)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(shippedDate, { shippedDate = it }, label = { Text("Shipped Date (yyyy-MM-dd)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(
                freight, { freight = it }, label = { Text("Freight") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(shipName, { shipName = it }, label = { Text("Ship Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(shipAddress, { shipAddress = it }, label = { Text("Ship Address") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(shipCity, { shipCity = it }, label = { Text("Ship City") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(shipRegion, { shipRegion = it }, label = { Text("Ship Region") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(shipPostalCode, { shipPostalCode = it }, label = { Text("Ship Postal Code") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(shipCountry, { shipCountry = it }, label = { Text("Ship Country") }, modifier = Modifier.fillMaxWidth())

            Button(
                onClick = {
                    onSave(
                        OrderDto(
                            orderId = existing?.orderId,
                            customerId = customerId.ifBlank { null },
                            employeeId = employeeId.toIntOrNull(),
                            orderDate = orderDate.ifBlank { null },
                            requiredDate = requiredDate.ifBlank { null },
                            shippedDate = shippedDate.ifBlank { null },
                            shipVia = existing?.shipVia,
                            freight = freight.toDoubleOrNull(),
                            shipName = shipName.ifBlank { null },
                            shipAddress = shipAddress.ifBlank { null },
                            shipCity = shipCity.ifBlank { null },
                            shipRegion = shipRegion.ifBlank { null },
                            shipPostalCode = shipPostalCode.ifBlank { null },
                            shipCountry = shipCountry.ifBlank { null },
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Save")
            }
        }
    }
}
