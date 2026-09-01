package org.course.mobile.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "auth")

// Android equivalent of frontend/src/js/auth.js's localStorage.getItem/setItem(TOKEN_KEY) -
// the JWT is the only thing persisted across app launches; everything else
// (currentAccount, authChecked) is in-memory UI state re-derived via GET /me on start.
class TokenDataStore(context: Context) {

    private val appContext = context.applicationContext
    private val tokenKey = stringPreferencesKey("auth_token")

    val tokenFlow: Flow<String?> = appContext.dataStore.data.map { it[tokenKey] }

    suspend fun currentToken(): String? = tokenFlow.first()

    suspend fun setToken(token: String) {
        appContext.dataStore.edit { it[tokenKey] = token }
    }

    suspend fun clearToken() {
        appContext.dataStore.edit { it.remove(tokenKey) }
    }
}
