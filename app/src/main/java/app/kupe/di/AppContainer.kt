package app.kupe.di

import android.app.Application
import app.kupe.BuildConfig
import app.kupe.core.network.AuthInterceptor
import app.kupe.core.network.ApiJson
import app.kupe.core.network.HostAllowlistInterceptor
import app.kupe.core.network.TokenAuthenticator
import app.kupe.core.security.SecureTokenStore
import app.kupe.data.google.CredentialManagerGoogleIdTokenProvider
import app.kupe.data.google.GoogleIdTokenProvider
import app.kupe.data.remote.AuthApi
import app.kupe.data.remote.Flags
import app.kupe.data.remote.GeoApi
import app.kupe.data.repository.AuthRepository
import app.kupe.data.repository.GeoRepository
import coil3.ImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class AppContainer(application: Application) {

    private val baseUrl: HttpUrl = BuildConfig.API_BASE_URL.toHttpUrl().also {
        check(BuildConfig.DEBUG || it.isHttps) { "Release builds must use an HTTPS API_BASE_URL" }
    }

    val tokenStore = SecureTokenStore(application)

    private val bareClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()

    private val apiClient = bareClient.newBuilder()
        .addInterceptor(AuthInterceptor(tokenStore, baseUrl.host))
        .authenticator(TokenAuthenticator(tokenStore, bareClient, baseUrl))
        .apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
            }
            // TODO (production): CertificatePinner via network_security_config pin-set.
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(apiClient)
        .addConverterFactory(ApiJson.asConverterFactory("application/json; charset=UTF-8".toMediaType()))
        .build()

    val authRepository = AuthRepository(retrofit.create(AuthApi::class.java), tokenStore)
    val geoRepository = GeoRepository(retrofit.create(GeoApi::class.java))

    val googleIdTokenProvider: GoogleIdTokenProvider =
        CredentialManagerGoogleIdTokenProvider(BuildConfig.GOOGLE_WEB_CLIENT_ID)
    private val flagClient = OkHttpClient.Builder()
        .addInterceptor(HostAllowlistInterceptor(setOf(Flags.HOST)))
        .build()

    val imageLoader: ImageLoader by lazy {
        ImageLoader.Builder(application)
            .components { add(OkHttpNetworkFetcherFactory(callFactory = { flagClient })) }
            .crossfade(true)
            .build()
    }
}
