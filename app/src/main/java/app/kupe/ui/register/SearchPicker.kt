package app.kupe.ui.register

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.kupe.R
import app.kupe.ui.components.FlagImage
import app.kupe.ui.components.KupeSearchField
import app.kupe.ui.theme.Blush
import app.kupe.ui.theme.Coral
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.text.style.TextAlign

@Composable
fun <T> SearchPicker(
    state: SearchState<T>,
    results: List<T>,
    onQueryChange: (String) -> Unit,
    onSelect: (T) -> Unit,
    itemKey: (T) -> String,
    itemLabel: (T) -> String,
    itemFlag: (T) -> String?,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        KupeSearchField(value = state.query, onValueChange = onQueryChange)
        if (state.query.isNotBlank()) {
            Column(Modifier.padding(top = 8.dp)) {
                Surface(
                    color = Blush,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().weight(1f, fill = false),
                ) {
                    when {
                        state.error -> PanelMessage(stringResource(R.string.search_error))
                        results.isEmpty() && state.loading -> Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(Modifier.size(24.dp), color = Coral, strokeWidth = 2.dp)
                        }
                        results.isEmpty() -> PanelMessage(stringResource(R.string.search_no_results))
                        else -> LazyColumn {
                            itemsIndexed(results, key = { _, item -> itemKey(item) }) { index, item ->
                                if (index > 0) HorizontalDivider(color = Coral.copy(alpha = 0.25f))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(role = Role.Button) { onSelect(item) }
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    FlagImage(itemFlag(item))
                                    Text(itemLabel(item), style = MaterialTheme.typography.bodyLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PanelMessage(text: String) {
    Text(text, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(16.dp))
}

@Composable
fun SelectedItemRow(
    label: String,
    flagUrl: String?,
    removeIcon: ImageVector,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(color = Blush, shape = RoundedCornerShape(10.dp), modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(start = 12.dp, top = 2.dp, bottom = 2.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            FlagImage(flagUrl)
            Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            IconButton(onClick = onRemove) {
                Icon(removeIcon, contentDescription = stringResource(R.string.remove_item, label), tint = Coral)
            }
        }
    }
}

val RemoveIcon: ImageVector get() = Icons.Default.Close
val DeleteIcon: ImageVector get() = Icons.Default.Delete
