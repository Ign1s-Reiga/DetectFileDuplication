package net.reiga7953.detectfileduplication

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator

@Composable
@Preview
fun DataTable(content: List<ItemData>) {
    val screens = listOf(DataTableResultScreen(content))

    Box(modifier = Modifier.padding(24.dp)) {
        Card(modifier = Modifier.fillMaxHeight()) {
            Navigator(screens)
        }
    }
}
