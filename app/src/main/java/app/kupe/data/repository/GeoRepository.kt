package app.kupe.data.repository

import app.kupe.core.network.safeCall
import app.kupe.data.remote.Country
import app.kupe.data.remote.GeoApi
import app.kupe.data.remote.Language
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class GeoRepository(private val api: GeoApi) {

    private val countriesLock = Mutex()
    private val languagesLock = Mutex()
    private var cachedCountries: List<Country>? = null
    private var cachedLanguages: List<Language>? = null

    suspend fun countries(): Result<List<Country>> = countriesLock.withLock {
        cachedCountries?.let { Result.success(it) }
            ?: safeCall { api.countries() }.onSuccess { cachedCountries = it }
    }

    suspend fun languages(): Result<List<Language>> = languagesLock.withLock {
        cachedLanguages?.let { Result.success(it) }
            ?: safeCall { api.languages() }.onSuccess { cachedLanguages = it }
    }
}