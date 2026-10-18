package app.kupe.ui.common

import androidx.annotation.StringRes
import app.kupe.R
import app.kupe.core.network.ApiException
import app.kupe.data.remote.ErrorCodes

@StringRes
fun Throwable.toMessageRes(): Int {
    val error = this as? ApiException ?: return R.string.error_generic
    return when (error.errorCode) {
        ErrorCodes.EMAIL_ALREADY_EXISTS -> R.string.error_email_taken
        ErrorCodes.USERNAME_ALREADY_EXISTS -> R.string.error_username_taken
        ErrorCodes.INVALID_CREDENTIALS -> R.string.error_invalid_credentials
        ErrorCodes.INVALID_REGISTRATION_TOKEN -> R.string.error_registration_expired
        ErrorCodes.INVALID_EXTERNAL_TOKEN -> R.string.error_google_failed
        ErrorCodes.EXTERNAL_EMAIL_NOT_VERIFIED -> R.string.error_google_unverified
        ErrorCodes.EXTERNAL_EMAIL_CONFLICT, ErrorCodes.EXTERNAL_IDENTITY_ALREADY_LINKED -> R.string.error_google_conflict
        else -> when (error.kind) {
            ApiException.Kind.NETWORK -> R.string.error_network
            ApiException.Kind.SERVER -> R.string.error_server
            ApiException.Kind.VALIDATION -> R.string.error_validation
            else -> R.string.error_generic
        }
    }
}
