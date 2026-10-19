package app.kupe.core.locale

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.os.LocaleListCompat
import java.util.Locale

enum class AppLanguage(val tag: String, val endonym: String) {
    ENGLISH("en", "English"),
    SPANISH("es", "Español"),
}

object AppLocale {
    fun selected(): AppLanguage? {
        val locales = AppCompatDelegate.getApplicationLocales()
        if (locales.isEmpty()) return null
        val language = locales[0]?.language ?: return null
        return AppLanguage.entries.firstOrNull { it.tag == language }
    }

    fun select(language: AppLanguage?) {
        AppCompatDelegate.setApplicationLocales(
            if (language == null) LocaleListCompat.getEmptyLocaleList()
            else LocaleListCompat.forLanguageTags(language.tag)
        )
    }
}

@Composable
fun currentLocale(): Locale = LocalConfiguration.current.locales[0]