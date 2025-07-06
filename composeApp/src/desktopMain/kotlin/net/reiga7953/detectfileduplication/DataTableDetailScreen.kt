package net.reiga7953.detectfileduplication

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            onClick = { navigator.popUntilRoot() }
        ) {
            Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = "Back")
        }
        HorizontalDivider()
        Column(modifier = Modifier.verticalScroll(state).padding(16.dp)) {
            repeat(files.size) {
                Text(text = files[it], fontSize = 14.sp)
            }
        }
    }
}
