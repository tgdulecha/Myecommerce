package org.course.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import org.course.mobile.data.local.TokenDataStore
import org.course.mobile.data.network.NetworkModule
import org.course.mobile.data.repository.AuthRepository
import org.course.mobile.ui.navigation.AppNav
import org.course.mobile.ui.theme.NorthwindMobileTheme
import org.course.mobile.ui.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val tokenDataStore = TokenDataStore(applicationContext)
        // One NetworkModule for the whole app - every service's API shares the same
        // authenticated OkHttpClient/TokenDataStore (see NetworkModule's own docs).
        val networkModule = NetworkModule(tokenDataStore)
        val authRepository = AuthRepository(networkModule.authApi, tokenDataStore)

        setContent {
            val authViewModel: AuthViewModel = viewModel(
                factory = viewModelFactory { initializer { AuthViewModel(authRepository) } },
            )

            NorthwindMobileTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNav(authViewModel, networkModule)
                }
            }
        }
    }
}
