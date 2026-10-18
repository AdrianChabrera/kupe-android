package app.kupe.data.google

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import app.kupe.BuildConfig
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException

class CredentialManagerGoogleIdTokenProvider(
    private val webClientId: String,
) : GoogleIdTokenProvider {

    override suspend fun requestIdToken(activityContext: Context): GoogleSignInResult {
        if (webClientId.isBlank() || webClientId.startsWith("REPLACE_")) {
            return GoogleSignInResult.NotConfigured
        }

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(GetSignInWithGoogleOption.Builder(webClientId).build())
            .build()

        return try {
            val response = CredentialManager.create(activityContext).getCredential(activityContext, request)
            val credential = response.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                GoogleSignInResult.Success(GoogleIdTokenCredential.createFrom(credential.data).idToken)
            } else {
                GoogleSignInResult.Failure
            }
        } catch (e: GetCredentialCancellationException) {
            logDebug(e)
            GoogleSignInResult.Cancelled
        } catch (e: GetCredentialException) {
            logDebug(e)
            GoogleSignInResult.Failure
        } catch (e: GoogleIdTokenParsingException) {
            logDebug(e)
            GoogleSignInResult.Failure
        }
    }

    private fun logDebug(e: Exception) {
        if (BuildConfig.DEBUG) Log.w("GoogleSignIn", "${e.javaClass.simpleName}: ${e.message}")
    }
}