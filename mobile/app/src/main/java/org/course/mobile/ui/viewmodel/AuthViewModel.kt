package org.course.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.course.mobile.data.network.AccountDto
import org.course.mobile.data.network.ApiResult
import org.course.mobile.data.network.RegisterRequest
import org.course.mobile.data.repository.AuthRepository

// Mirrors auth.js's currentAccount/authChecked refs (Session) plus each Vue screen's
// own submitting/error refs (Form) - kept in one ViewModel since the whole app is
// just these three screens for v1.
sealed class Session {
    data object Checking : Session()
    data object SignedOut : Session()
    data class SignedIn(val account: AccountDto) : Session()
}

data class FormState(
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
)

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _session = MutableStateFlow<Session>(Session.Checking)
    val session: StateFlow<Session> = _session.asStateFlow()

    private val _form = MutableStateFlow(FormState())
    val form: StateFlow<FormState> = _form.asStateFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            when (val result = repository.checkSession()) {
                null -> _session.value = Session.SignedOut
                is ApiResult.Success -> _session.value = Session.SignedIn(result.data)
                is ApiResult.Failure -> _session.value = Session.SignedOut
            }
        }
    }

    fun signIn(email: String, password: String) {
        _form.update { FormState(isSubmitting = true) }
        viewModelScope.launch {
            when (val result = repository.login(email, password)) {
                is ApiResult.Success -> {
                    _form.value = FormState()
                    _session.value = Session.SignedIn(result.data)
                }
                is ApiResult.Failure -> _form.value = FormState(errorMessage = result.message)
            }
        }
    }

    fun signUp(request: RegisterRequest) {
        _form.update { FormState(isSubmitting = true) }
        viewModelScope.launch {
            when (val result = repository.register(request)) {
                is ApiResult.Success -> {
                    _form.value = FormState()
                    _session.value = Session.SignedIn(result.data)
                }
                is ApiResult.Failure -> _form.value = FormState(errorMessage = result.message)
            }
        }
    }

    fun clearError() {
        _form.update { it.copy(errorMessage = null) }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _session.value = Session.SignedOut
        }
    }
}
