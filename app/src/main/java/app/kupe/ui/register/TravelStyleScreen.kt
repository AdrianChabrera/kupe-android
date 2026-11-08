package app.kupe.ui.register

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.kupe.R
import app.kupe.ui.components.KupeButton
import app.kupe.ui.components.KupeButtonStyle
import app.kupe.ui.components.NavigationFooter
import app.kupe.ui.components.RegistrationScaffold
import app.kupe.ui.components.StepHeading
import app.kupe.ui.theme.Blush
import app.kupe.ui.theme.Coral
import kotlinx.coroutines.launch

private const val LOOP_PAGES = 10_000

@Composable
fun TravelStyleScreen(
    viewModel: RegistrationViewModel,
    stylesViewModel: TravelStyleViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val stylesState by stylesViewModel.state.collectAsStateWithLifecycle()
    BackHandler(onBack = onBack)

    RegistrationScaffold(
        step = 3,
        footer = {
            NavigationFooter(
                onBack = onBack,
                onNext = onNext,
            )
        },
    ) {
        StepHeading(stringResource(R.string.travel_style_title), stringResource(R.string.travel_style_subtitle))
        Spacer(Modifier.height(24.dp))
        when {
            stylesState.loading -> CircularProgressIndicator(color = Coral, modifier = Modifier.padding(16.dp))
            stylesState.error -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(stringResource(R.string.search_error), style = MaterialTheme.typography.bodyMedium)
                KupeButton(
                    stringResource(R.string.action_retry),
                    stylesViewModel::load,
                    Modifier.fillMaxWidth(),
                    KupeButtonStyle.Neutral,
                )
            }
            stylesState.styles.isNotEmpty() -> TravelStyleCarousel(
                styles = stylesState.styles,
                selectedCode = state.travelStyleCode,
                onStyleShown = { viewModel.selectTravelStyle(it.code) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun TravelStyleCarousel(
    styles: List<TravelStyleUi>,
    selectedCode: String?,
    onStyleShown: (TravelStyleUi) -> Unit,
    modifier: Modifier = Modifier,
    ) {
    val count = styles.size
    val looping = count > 1
    val startIndex = styles.indexOfFirst { it.code == selectedCode }.coerceAtLeast(0)
    val pagerState = rememberPagerState(
        initialPage = if (looping) LOOP_PAGES / 2 - (LOOP_PAGES / 2) % count + startIndex else 0,
        pageCount = { if (looping) LOOP_PAGES else count },
    )
    val scope = rememberCoroutineScope()
    val currentOnStyleShown by rememberUpdatedState(onStyleShown)

    LaunchedEffect(pagerState, styles) {
        snapshotFlow { pagerState.currentPage }
            .collect { page -> currentOnStyleShown(styles[page % count]) }
    }

    fun scrollBy(delta: Int) {
        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + delta) }
    }

    HorizontalPager(
        state = pagerState,
        modifier = modifier,
        pageSpacing = 12.dp,
        verticalAlignment = Alignment.Top,
    ) { page ->
        TravelStyleCard(
            style = styles[page % count],
            showArrows = looping,
            onPrevious = { scrollBy(-1) },
            onNext = { scrollBy(1) },
        )
    }
}

@Composable
private fun TravelStyleCard(
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