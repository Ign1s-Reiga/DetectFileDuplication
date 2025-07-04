package net.reiga7953.detectfileduplication

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
@Preview
fun DataTable(content: List<ItemData>) {
    Box(modifier = Modifier.padding(24.dp)) {
        Card {
            val state = rememberLazyListState()
            DataTableItem("Hash", "Dup. Count", true)
            HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))
            LazyColumn(state = state) {
                items(content, { it.hash + "-" +  it.files.size }) {
                    DataTableItem(it.hash, it.files.size.toString())
                }
            }

            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(state)
            )
        }
    }
}

@Composable
@Preview
fun DataTableItem(hash: String, count: String, isTopRow: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().height(50.dp).padding(horizontal = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(2) {
            TextButton(
                onClick = {},
                shape = buttonShape(it, isTopRow),
                modifier = Modifier.weight(1f)
            ) {
                Text(if (it == 0) hash else count)
            }
        }
    }
}

data class ItemData(val hash: String, val files: List<String>)

val buttonShape = { idx: Int, isTopRow: Boolean ->
    if (isTopRow) {
        if (idx == 0) {
            RoundedCornerShape(topStart = 12.dp, topEnd = 4.dp, bottomStart = 4.dp, bottomEnd = 4.dp)
        } else {
            RoundedCornerShape(topStart = 4.dp, topEnd = 12.dp, bottomStart = 4.dp, bottomEnd = 4.dp)
        }
    } else {
        RoundedCornerShape(4.dp)
    }
}
