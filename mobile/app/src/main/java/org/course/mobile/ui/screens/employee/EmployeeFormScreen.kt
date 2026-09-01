package org.course.mobile.ui.screens.employee

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
import androidx.compose.material3.MaterialTheme
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
import org.course.mobile.data.network.dto.EmployeeDto

// Photo is deliberately not editable here - see the mobile-app plan's Employee
// scoping note (no image picker in v1); create/edit always sends photo = null.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeFormScreen(
    existing: EmployeeDto?,
    onBack: () -> Unit,
    onSave: (EmployeeDto) -> Unit,
) {
    var firstName by remember { mutableStateOf(existing?.firstName.orEmpty()) }
    var lastName by remember { mutableStateOf(existing?.lastName.orEmpty()) }
    var title by remember { mutableStateOf(existing?.title.orEmpty()) }
    var titleOfCourtesy by remember { mutableStateOf(existing?.titleOfCourtesy.orEmpty()) }
    var homePhone by remember { mutableStateOf(existing?.homePhone.orEmpty()) }
    var address by remember { mutableStateOf(existing?.address.orEmpty()) }
    var city by remember { mutableStateOf(existing?.city.orEmpty()) }
    var region by remember { mutableStateOf(existing?.region.orEmpty()) }
    var postalCode by remember { mutableStateOf(existing?.postalCode.orEmpty()) }
    var country by remember { mutableStateOf(existing?.country.orEmpty()) }
    var notes by remember { mutableStateOf(existing?.notes.orEmpty()) }
    var reportsToId by remember { mutableStateOf(existing?.reportsToId?.toString().orEmpty()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (existing == null) "New Employee" else "Edit Employee") },
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
            OutlinedTextField(firstName, { firstName = it }, label = { Text("First Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(lastName, { lastName = it }, label = { Text("Last Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(title, { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(titleOfCourtesy, { titleOfCourtesy = it }, label = { Text("Title Of Courtesy") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(
                homePhone, { homePhone = it }, label = { Text("Home Phone") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(address, { address = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(city, { city = it }, label = { Text("City") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(region, { region = it }, label = { Text("Region") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(postalCode, { postalCode = it }, label = { Text("Postal Code") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(country, { country = it }, label = { Text("Country") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(
                reportsToId, { reportsToId = it }, label = { Text("Reports To (Employee ID)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(notes, { notes = it }, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth())

            Button(
                onClick = {
                    onSave(
                        EmployeeDto(
                            employeeId = existing?.employeeId,
                            firstName = firstName,
                            lastName = lastName,
                            title = title.ifBlank { null },
                            titleOfCourtesy = titleOfCourtesy.ifBlank { null },
                            homePhone = homePhone.ifBlank { null },
                            address = address.ifBlank { null },
                            city = city.ifBlank { null },
                            region = region.ifBlank { null },
                            postalCode = postalCode.ifBlank { null },
                            country = country.ifBlank { null },
                            notes = notes.ifBlank { null },
                            reportsToId = reportsToId.toIntOrNull(),
                            birthDate = existing?.birthDate,
                            hireDate = existing?.hireDate,
                            photo = null,
                            photoPath = existing?.photoPath,
                            reportsToName = existing?.reportsToName,
                            extension = existing?.extension,
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
