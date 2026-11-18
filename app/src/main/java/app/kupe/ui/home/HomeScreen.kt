package app.kupe.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.kupe.R
import app.kupe.ui.components.KupeButton
import app.kupe.ui.components.KupeButtonStyle
import app.kupe.ui.map.components.KupeMap

@Composable
fun HomeScreen(onLogout: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        KupeMap(modifier = Modifier.fillMaxSize())

        KupeButton(
            text = stringResource(R.string.action_logout),
            onClick = onLogout,
            style = KupeButtonStyle.Neutral,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .systemBarsPadding()
                .padding(16.dp),
        )
    }
}