package net.reiga7953.detectfileduplication

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

data class DataTableDetailScreen(val files: List<String>): Screen {
    @Composable
    @Preview
    override fun Content() {
        val state = rememberScrollState()
        val navigator = LocalNavigator.currentOrThrow

        IconButton(
            modifier = Modifier.padding(start = 16.dp),
            onClick = { navigator.popUntilRoot() }
        ) {
            Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = "Back")
        }
        Column(modifier = Modifier.verticalScroll(state)) {
            repeat(files.size) {
                Text(files[it], modifier = Modifier.padding(8.dp))
            }
        }
    }
}
