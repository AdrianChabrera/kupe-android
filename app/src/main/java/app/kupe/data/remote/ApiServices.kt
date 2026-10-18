package app.kupe.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthApi {
    @POST("api/auth/register/start")
    suspend fun startRegistration(@Body body: RegisterRequest): RegistrationTokenResponse

    @POST("api/auth/register/complete")
    suspend fun completeRegistration(@Body body: CompleteRegistrationRequest): TokenResponse

    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): TokenResponse

    @POST("api/auth/external/{provider}")
    suspend fun externalLogin(@Path("provider") provider: String, @Body body: ExternalLoginRequest): ExternalAuthResponse
}

interface GeoApi {
    @GET("api/countries")
    suspend fun countries(): List<Country>

    @GET("api/countries/languages")
    suspend fun languages(): List<Language>
}