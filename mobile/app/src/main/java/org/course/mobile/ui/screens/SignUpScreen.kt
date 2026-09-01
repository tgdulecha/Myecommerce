package org.course.mobile.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import org.course.mobile.data.network.RegisterRequest
import org.course.mobile.ui.viewmodel.FormState

// Mirrors frontend/src/views/SignUp.vue: company-info fields + account fields,
// client-side password/confirm-password match and 8-char minimum checked before
// ever calling the network - same UX as the web form's `required minlength="8"` /
// manual confirmPassword check.
@Composable
fun SignUpScreen(
    formState: FormState,
    onSignUp: (RegisterRequest) -> Unit,
    onClearError: () -> Unit,
    onNavigateToSignIn: () -> Unit,
) {
    var companyName by remember { mutableStateOf("") }
    var contactName by remember { mutableStateOf("") }
    var contactTitle by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    val displayedError = localError ?: formState.errorMessage

    fun submit() {
        localError = null
        onClearError()

        if (password != confirmPassword) {
            localError = "Passwords do not match."
            return
        }
        if (password.length < 8) {
            localError = "Password must be at least 8 characters."
            return
        }

        onSignUp(
            RegisterRequest(
                email = email.trim(),
                password = password,
                companyName = companyName,
                contactName = contactName.ifBlank { null },
                contactTitle = contactTitle.ifBlank { null },
                address = address.ifBlank { null },
                city = city.ifBlank { null },
                region = region.ifBlank { null },
                postalCode = postalCode.ifBlank { null },
                country = country.ifBlank { null },
                phone = phone.ifBlank { null },
            )
        )
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Create an Account", style = MaterialTheme.typography.headlineMedium)

            Text("Company Info", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(companyName, { companyName = it }, label = { Text("Company Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(contactName, { contactName = it }, label = { Text("Contact Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(contactTitle, { contactTitle = it }, label = { Text("Contact Title") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(
                phone, { phone = it }, label = { Text("Phone") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(address, { address = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(city, { city = it }, label = { Text("City") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(region, { region = it }, label = { Text("Region") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(postalCode, { postalCode = it }, label = { Text("Postal Code") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(country, { country = it }, label = { Text("Country") }, modifier = Modifier.fillMaxWidth())

            Text("Account", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                email, { email = it }, label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                password, { password = it }, label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                confirmPassword, { confirmPassword = it }, label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
            )

            displayedError?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = ::submit,
                enabled = !formState.isSubmitting,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (formState.isSubmitting) "Creating account…" else "Sign Up")
            }

            TextButton(onClick = onNavigateToSignIn, modifier = Modifier.fillMaxWidth()) {
                Text("Already have an account? Sign in")
            }
        }
    }
}
