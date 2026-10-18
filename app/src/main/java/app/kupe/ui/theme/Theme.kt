package app.kupe.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// The mockups only define a light palette, so the theme is intentionally light-only
// (no dynamic color) to keep the brand identity consistent.
private val KupeColorScheme = lightColorScheme(
    primary = Coral,
    onPrimary = Ink,
    primaryContainer = Blush,
    onPrimaryContainer = Ink,
    secondary = NeutralGrey,
    onSecondary = Ink,
    background = Rose50,
    onBackground = Ink,
    surface = Rose50,
    onSurface = Ink,
    surfaceVariant = Blush,
    onSurfaceVariant = Ink,
    outline = CoralSoft,
    error = ErrorRed,
)

@Composable
fun KupeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = KupeColorScheme,
        typography = Typography,
        content = content,
    )
}
