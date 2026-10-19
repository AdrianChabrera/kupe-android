package app.kupe.ui.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.kupe.R
import app.kupe.ui.components.AppLogoPlaceholder
import app.kupe.ui.components.KupeButton
import app.kupe.ui.components.KupeButtonStyle
import app.kupe.ui.components.LanguageSelector

@Composable
fun WelcomeScreen(onLogin: () -> Unit, onRegister: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LanguageSelector(Modifier.align(Alignment.End))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AppLogoPlaceholder()
            Spacer(Modifier.height(12.dp))
            Text(stringResource(R.string.welcome_tagline), style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            KupeButton(stringResource(R.string.action_login), onLogin, Modifier.weight(1f), KupeButtonStyle.Neutral)
            KupeButton(stringResource(R.string.action_register), onRegister, Modifier.weight(1f))
        }
    }
}