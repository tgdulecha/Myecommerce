package org.course.mobile.ui.screens.payment

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
import org.course.mobile.data.network.dto.PaymentDto

// Server forces status to PENDING on create regardless of what's sent (see
// PaymentController), so this form only collects the fields that actually matter:
// orderId, customerEmail, amount, method.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentFormScreen(
    defaultOrderId: Int?,
    onBack: () -> Unit,
    onSave: (PaymentDto) -> Unit,
) {
    var orderId by remember { mutableStateOf(defaultOrderId?.toString().orEmpty()) }
    var customerEmail by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var method by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Payment") },
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
                orderId, { orderId = it }, label = { Text("Order ID") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                customerEmail, { customerEmail = it }, label = { Text("Customer Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                amount, { amount = it }, label = { Text("Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(method, { method = it }, label = { Text("Method (e.g. CARD)") }, modifier = Modifier.fillMaxWidth())

            Button(
                onClick = {
                    val oid = orderId.toIntOrNull() ?: return@Button
                    val amt = amount.toDoubleOrNull() ?: return@Button
                    onSave(PaymentDto(orderId = oid, customerEmail = customerEmail.trim(), amount = amt, method = method))
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Create Payment")
            }
        }
    }
}
