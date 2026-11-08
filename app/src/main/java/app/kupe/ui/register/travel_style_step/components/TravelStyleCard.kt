package app.kupe.ui.register.travel_style_step.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.kupe.R
import app.kupe.ui.register.travel_style_step.TravelStyleUi
import app.kupe.ui.theme.Blush
import app.kupe.ui.theme.Coral

@Composable
fun TravelStyleCard(
    style: TravelStyleUi,
    showArrows: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    Surface(
        color = Blush,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(style.nameRes),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (showArrows) {
                    IconButton(
                        onClick = onPrevious,
                        modifier = Modifier.size(72.dp),
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowLeft,
                            contentDescription = stringResource(R.string.travel_style_previous),
                            tint = Coral,
                            modifier = Modifier.size(64.dp),
                        )
                    }
                } else {
                    Spacer(Modifier.width(72.dp))
                }
                Box(Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                    style.iconRes?.let { icon ->
                        Image(
                            painter = painterResource(icon),
                            contentDescription = null,
                            modifier = Modifier.fillMaxHeight(),
                            contentScale = ContentScale.Fit,
                        )
                    }
                }
                if (showArrows) {
                    IconButton(
                        onClick = onNext,
                        modifier = Modifier.size(72.dp),
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowRight,
                            contentDescription = stringResource(R.string.travel_style_next),
                            tint = Coral,
                            modifier = Modifier.size(64.dp),
                        )
                    }
                } else {
                    Spacer(Modifier.width(72.dp))
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(style.descriptionRes),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify,
            )
        }
    }
}