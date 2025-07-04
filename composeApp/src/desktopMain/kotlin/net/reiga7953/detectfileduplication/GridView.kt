package net.reiga7953.detectfileduplication

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.PersistentList

/**
 * GridView for MainView input form.
 */
@Composable
@Preview
fun GridView(listOfContent: PersistentList<@Composable () -> Unit>) {
    var maxWidth by remember { mutableStateOf(584) } // Default max width
    FlowRow(
        // FlowRow.width - (TonalButton.width + spacing * 2) = TextField.maxWidth
        modifier = Modifier.onSizeChanged { maxWidth = it.width - (100 + 18 * 2) },
        horizontalArrangement = Arrangement.spacedBy(18.dp),
        maxItemsInEachRow = 2,
        maxLines = 2
    ) {
        listOfContent.forEachIndexed { idx, composable ->
            Box(
                modifier = Modifier.height(70.dp).then(
                    when {
                        idx % 2 == 0 -> Modifier.weight(1.0F).widthIn(max = maxWidth.dp)
                        else -> Modifier.width(100.dp)
                    }
                ),
                contentAlignment = Alignment.CenterStart
            ) {
                composable()
            }
        }
    }
}
