package org.course.mobile.data.network

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.course.mobile.BuildConfig
import org.course.mobile.data.local.TokenDataStore
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

// Constructed once (in MainActivity) around the app's single TokenDataStore, so
// every service's API shares one authenticated client: every ecommerce-service and
// payment-service endpoint requires the bearer token (only auth-service's
// register/login are public), so attaching it here once replaces what would
// otherwise be a @Header param repeated on ~20 individual endpoints.
class NetworkModule(private val tokenDataStore: TokenDataStore) {

    private val json = Json { ignoreUnknownKeys = true }

    private val authInterceptor = Interceptor { chain ->
        val token = runBlocking { tokenDataStore.currentToken() }
        val request = if (token != null) {
            chain.request().newBuilder().addHeader("Authorization", "Bearer $token").build()
        } else {
            chain.request()
        }
        chain.proceed(request)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        })
        .build()

    private fun <T> buildApi(baseUrl: String, service: Class<T>): T =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(service)

    val authApi: AuthApi = buildApi(BuildConfig.AUTH_BASE_URL, AuthApi::class.java)
    val categoryApi: CategoryApi = buildApi(BuildConfig.ECOMMERCE_BASE_URL, CategoryApi::class.java)
    val productApi: ProductApi = buildApi(BuildConfig.ECOMMERCE_BASE_URL, ProductApi::class.java)
    val employeeApi: EmployeeApi = buildApi(BuildConfig.ECOMMERCE_BASE_URL, EmployeeApi::class.java)
    val orderApi: OrderApi = buildApi(BuildConfig.ECOMMERCE_BASE_URL, OrderApi::class.java)
    val orderDetailsApi: OrderDetailsApi = buildApi(BuildConfig.ECOMMERCE_BASE_URL, OrderDetailsApi::class.java)
    val paymentApi: PaymentApi = buildApi(BuildConfig.PAYMENT_BASE_URL, PaymentApi::class.java)
}
