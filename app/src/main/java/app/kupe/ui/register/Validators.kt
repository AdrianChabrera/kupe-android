package app.kupe.ui.register

import android.util.Patterns
import androidx.annotation.StringRes
import app.kupe.R

object Validators {
    private val USERNAME_REGEX = Regex("^[a-zA-Z0-9_.-]+$")

    @StringRes
    fun email(value: String): Int? = when {
        value.isBlank() -> R.string.error_email_required
        !Patterns.EMAIL_ADDRESS.matcher(value.trim()).matches() -> R.string.error_email_invalid
        else -> null
    }

    @StringRes
    fun password(value: String): Int? = when {
        value.isEmpty() -> R.string.error_password_required
        value.length < 6 -> R.string.error_password_short
        value.length > 128 -> R.string.error_password_long
        else -> null
    }

    @StringRes
    fun username(value: String): Int? = when {
        value.isEmpty() -> R.string.error_username_required
        value.length !in 3..32 -> R.string.error_username_length
        !USERNAME_REGEX.matches(value) -> R.string.error_username_chars
        else -> null
    }
}
