package net.reiga7953.detectfileduplication

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.PersistentList

/**
 * GridView for MainView input form.
 */
@Composable
@Preview
fun GridView(listOfContent: PersistentList<@Composable () -> Unit>) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        maxItemsInEachRow = 2,
    ) {
        listOfContent.forEachIndexed { idx, composable ->
            Surface(
                modifier = Modifier.height(70.dp).then(
                    when {
                        idx == listOfContent.size - 1 || idx % 2 != 0 -> Modifier.weight(1f)
                        else -> Modifier.width(200.dp)
                    }
                ),
                color = Color(0xFFFFFFFF)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = when {
                        idx == listOfContent.size - 1 -> Alignment.CenterEnd
                        else -> Alignment.CenterStart
                    }
                ) {
                    composable()
                }
            }
        }
    }
}
