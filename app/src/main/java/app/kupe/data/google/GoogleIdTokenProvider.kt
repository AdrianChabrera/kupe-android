package app.kupe.data.google

import android.content.Context

sealed interface GoogleSignInResult {
    data class Success(val idToken: String) : GoogleSignInResult {
        override fun toString() = "Success"
    }
    data object Cancelled : GoogleSignInResult
    data object NotConfigured : GoogleSignInResult
    data object Failure : GoogleSignInResult
}

interface GoogleIdTokenProvider {
    suspend fun requestIdToken(activityContext: Context): GoogleSignInResult
}
