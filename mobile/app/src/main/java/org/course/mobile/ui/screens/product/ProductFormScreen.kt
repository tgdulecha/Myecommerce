package org.course.mobile.ui.screens.product

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
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
import org.course.mobile.data.network.dto.ProductDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(
    existing: ProductDto?,
    onBack: () -> Unit,
    onSave: (ProductDto) -> Unit,
) {
    var name by remember { mutableStateOf(existing?.productName.orEmpty()) }
    var categoryId by remember { mutableStateOf(existing?.categoryId?.toString().orEmpty()) }
    var supplierId by remember { mutableStateOf(existing?.supplierId?.toString().orEmpty()) }
    var quantityPerUnit by remember { mutableStateOf(existing?.quantityPerUnit.orEmpty()) }
    var unitPrice by remember { mutableStateOf(existing?.unitPrice?.toString().orEmpty()) }
    var unitsInStock by remember { mutableStateOf(existing?.unitsInStock?.toString().orEmpty()) }
    var reorderLevel by remember { mutableStateOf(existing?.reorderLevel?.toString().orEmpty()) }
    var discontinued by remember { mutableStateOf(existing?.discontinued ?: false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (existing == null) "New Product" else "Edit Product") },
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
            OutlinedTextField(name, { name = it }, label = { Text("Product Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(
                categoryId, { categoryId = it }, label = { Text("Category ID") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                supplierId, { supplierId = it }, label = { Text("Supplier ID") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(quantityPerUnit, { quantityPerUnit = it }, label = { Text("Quantity Per Unit") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(
                unitPrice, { unitPrice = it }, label = { Text("Unit Price") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                unitsInStock, { unitsInStock = it }, label = { Text("Units In Stock") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                reorderLevel, { reorderLevel = it }, label = { Text("Reorder Level") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )

            Row {
                Checkbox(checked = discontinued, onCheckedChange = { discontinued = it })
                Text("Discontinued", modifier = Modifier.padding(top = 12.dp))
            }

            Button(
                onClick = {
                    onSave(
                        ProductDto(
                            productId = existing?.productId,
                            productName = name,
                            categoryId = categoryId.toIntOrNull(),
                            supplierId = supplierId.toIntOrNull(),
                            quantityPerUnit = quantityPerUnit.ifBlank { null },
                            unitPrice = unitPrice.toDoubleOrNull(),
                            unitsInStock = unitsInStock.toShortOrNull(),
                            reorderLevel = reorderLevel.toShortOrNull(),
                            discontinued = discontinued,
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
