package app.kupe.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.kupe.R
import app.kupe.core.locale.AppLanguage
import app.kupe.core.locale.AppLocale
import app.kupe.ui.theme.Coral

@Composable
fun LanguageSelector(modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(AppLocale.selected()) }

    fun choose(language: AppLanguage?) {
        expanded = false
        selected = language
        AppLocale.select(language)
    }

    Box(modifier) {
        TextButton(
            onClick = { expanded = true },
            colors = ButtonDefaults.textButtonColors(contentColor = Coral),
        ) {
            Icon(Icons.Default.Language, contentDescription = stringResource(R.string.language_selector_description))
            Spacer(Modifier.width(6.dp))
            Text(selected?.endonym ?: stringResource(R.string.language_system_default))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            LanguageMenuItem(
                label = stringResource(R.string.language_system_default),
                checked = selected == null,
                onClick = { choose(null) },
            )
            AppLanguage.entries.forEach { language ->
                LanguageMenuItem(
                    label = language.endonym,
                    checked = selected == language,
                    onClick = { choose(language) },
                )
            }
        }
    }
}

@Composable
private fun LanguageMenuItem(label: String, checked: Boolean, onClick: () -> Unit) {
    DropdownMenuItem(
        text = { Text(label) },
        onClick = onClick,
        trailingIcon = { if (checked) Icon(Icons.Default.Check, contentDescription = null) },
    )
}