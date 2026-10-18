package app.kupe.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import app.kupe.R
import app.kupe.ui.theme.Coral
import app.kupe.ui.theme.CoralSoft
import app.kupe.ui.theme.FieldSurface
import app.kupe.ui.theme.Ink
import app.kupe.ui.theme.NeutralGrey

enum class KupeButtonStyle { Primary, Neutral, Outlined }

@Composable
fun KupeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: KupeButtonStyle = KupeButtonStyle.Primary,
    enabled: Boolean = true,
    loading: Boolean = false,
) {
    val container = when (style) {
        KupeButtonStyle.Primary -> Coral
        KupeButtonStyle.Neutral -> NeutralGrey
        KupeButtonStyle.Outlined -> FieldSurface
    }
    Button(
        onClick = { if (!loading) onClick() },
        modifier = modifier.heightIn(min = 48.dp),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        border = if (style == KupeButtonStyle.Outlined) BorderStroke(1.dp, if (enabled) Coral else CoralSoft) else null,
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            contentColor = Ink,
            disabledContainerColor = container.copy(alpha = 0.45f),
            disabledContentColor = Ink.copy(alpha = 0.5f),
        ),
    ) {
        if (loading) {
            CircularProgressIndicator(Modifier.size(20.dp), color = Ink, strokeWidth = 2.dp)
        } else {
            Text(text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun GoogleSignInButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String = stringResource(R.string.sign_up_with),
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = 52.dp),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, CoralSoft),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White, contentColor = Ink),
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.width(12.dp))
        Image(
            painter = painterResource(R.drawable.google_logo),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )
    }
}