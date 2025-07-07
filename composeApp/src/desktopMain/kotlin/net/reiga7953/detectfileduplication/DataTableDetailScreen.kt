package net.reiga7953.detectfileduplication

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import java.nio.file.Path

data class DataTableDetailScreen(val files: MutableList<Path>): Screen {
    @Composable
    @Preview
    override fun Content() {
        val scrollState = rememberScrollState()
        var dialogVisibility by remember { mutableStateOf(false) }
        var selectedFilePath by remember { mutableStateOf<Path?>(null) }
        val navigator = LocalNavigator.currentOrThrow

        IconButton(onClick = { navigator.popUntilRoot() }) {
            Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = "Back")
        }
        HorizontalDivider()
        Column(modifier = Modifier.verticalScroll(scrollState)) {
            repeat(files.size) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    Text(text = files[it].fileName.toString(), modifier = Modifier.weight(1F).align(Alignment.CenterVertically), fontSize = 14.sp)
                    IconButton(onClick = {
                        selectedFilePath = files[it]
                        dialogVisibility = true
                    }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete file")
                    }
                }
                HorizontalDivider()
            }
        }
        // TODO: add doNotAskConfirmation setting
        ConfirmationDialog(
            visible = dialogVisibility,
            onConfirm = {
                dialogVisibility = false
                // TODO: show toast with result, success or failure.
                selectedFilePath?.let {
                    deleteFile(it)
                    files.remove(it)
                }
            },
            onDismiss = { dialogVisibility = false },
            text = { Text("Do you really want to delete this file?") },
        )
    }
}
