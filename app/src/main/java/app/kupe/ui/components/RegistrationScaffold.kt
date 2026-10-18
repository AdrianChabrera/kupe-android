package app.kupe.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.kupe.R
import app.kupe.ui.theme.Coral
import app.kupe.ui.theme.ErrorRed

const val REGISTRATION_STEPS = 7

@Composable
fun RegistrationScaffold(
    step: Int,
    modifier: Modifier = Modifier,
    footer: @Composable ColumnScope.() -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .imePadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.register_title),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(12.dp))
        StepIndicator(current = step, total = REGISTRATION_STEPS)
        Spacer(Modifier.height(24.dp))
        Column(Modifier.weight(1f).fillMaxWidth(), content = content)
        Spacer(Modifier.height(12.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = footer,
        )
    }
}

@Composable
fun StepIndicator(current: Int, total: Int, modifier: Modifier = Modifier) {
    val description = stringResource(R.string.step_indicator, current, total)
    Row(
        modifier = modifier.semantics { contentDescription = description },
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for (index in 1..total) {
            val active = index == current
            val width by animateDpAsState(if (active) 24.dp else 8.dp, label = "stepDotWidth")
            Box(
                Modifier
                    .width(width)
                    .height(8.dp)
                    .clip(CircleShape)
                    .then(if (active) Modifier.background(Coral) else Modifier.border(1.dp, Coral, CircleShape)),
            )
        }
    }
}

@Composable
fun StepHeading(title: String, subtitle: String? = null, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
        if (subtitle != null) {
            Spacer(Modifier.height(8.dp))
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun HintText(text: String, modifier: Modifier = Modifier) {
    Text(text, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center, modifier = modifier)
}

@Composable
fun FormError(@androidx.annotation.StringRes messageRes: Int?, modifier: Modifier = Modifier) {
    if (messageRes != null) {
        Text(
            text = stringResource(messageRes),
            color = ErrorRed,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun AppLogoPlaceholder(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.app_name),
        color = Coral,
        fontSize = 36.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier,
    )
}

@Composable
fun PlaceholderBox(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .drawBehind {
                drawRoundRect(
                    color = Coral,
                    cornerRadius = CornerRadius(16.dp.toPx()),
                    style = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 14f))),
                )
            }
            .clip(RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text(stringResource(R.string.placeholder_label), color = Coral, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun NavigationFooter(
    onBack: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
    onSkip: (() -> Unit)? = null,
    skipLabel: String = stringResource(R.string.action_skip),
    nextLabel: String = stringResource(R.string.action_next),
    nextEnabled: Boolean = true,
    loading: Boolean = false,
    showChangeLaterHint: Boolean = true,
) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        if (showChangeLaterHint) HintText(stringResource(R.string.can_change_later))
        if (onSkip != null) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                KupeButton(stringResource(R.string.action_go_back), onBack, Modifier.weight(1f), KupeButtonStyle.Neutral, enabled = !loading)
                KupeButton(skipLabel, onSkip, Modifier.weight(1f), KupeButtonStyle.Outlined, enabled = !loading)
            }
            KupeButton(nextLabel, onNext, Modifier.fillMaxWidth(), enabled = nextEnabled, loading = loading)
        } else {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                KupeButton(stringResource(R.string.action_go_back), onBack, Modifier.weight(1f), KupeButtonStyle.Neutral, enabled = !loading)
                KupeButton(nextLabel, onNext, Modifier.weight(1f), enabled = nextEnabled, loading = loading)
            }
        }
    }
}
