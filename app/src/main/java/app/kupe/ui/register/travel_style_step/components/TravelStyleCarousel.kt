package app.kupe.ui.register.travel_style_step.components

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.kupe.ui.register.travel_style_step.TravelStyleUi
import kotlinx.coroutines.launch

private const val LOOP_PAGES = 10_000

@Composable
fun TravelStyleCarousel(
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