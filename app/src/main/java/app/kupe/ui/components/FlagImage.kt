package app.kupe.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import app.kupe.ui.theme.Coral
import coil3.compose.AsyncImage

@Composable
fun FlagImage(url: String?, modifier: Modifier = Modifier) {
    val fallback = rememberVectorPainter(Icons.Default.Language)
    Box(
        modifier = modifier
            .size(width = 36.dp, height = 26.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color.White.copy(alpha = 0.6f)),
    ) {
        if (url != null) {
            AsyncImage(
                model = url,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                error = fallback,
            )
        } else {
            Icon(Icons.Default.Language, contentDescription = null, tint = Coral, modifier = Modifier.align(Alignment.Center))
        }
    }
}
