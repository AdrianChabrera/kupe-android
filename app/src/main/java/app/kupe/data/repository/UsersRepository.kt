package app.kupe.data.repository

import app.kupe.core.network.safeCall
import app.kupe.data.remote.UsersApi
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class UsersRepository(private val api: UsersApi) {
    private val lock = Mutex()
    private var cachedStyles: List<String>? = null
    private var cachedInterests: List<String>? = null

    suspend fun travelStyles(): Result<List<String>> = lock.withLock {
        cachedStyles?.let { Result.success(it) }
            ?: safeCall { api.travelStyles() }.onSuccess { cachedStyles = it }
    }

    suspend fun travelInterests(): Result<List<String>> = lock.withLock {
        cachedInterests?.let { Result.success(it) }
            ?: safeCall { api.travelInterests() }.onSuccess { cachedInterests = it }
    }
}