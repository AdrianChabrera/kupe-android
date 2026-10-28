package app.kupe.data.repository

import app.kupe.core.network.safeCall
import app.kupe.data.remote.UsersApi
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class UsersRepository(private val api: UsersApi) {
    private val lock = Mutex()
    private var cached: List<String>? = null

    suspend fun travelStyles(): Result<List<String>> = lock.withLock {
        cached?.let { Result.success(it) }
            ?: safeCall { api.travelStyles() }.onSuccess { cached = it }
    }
}