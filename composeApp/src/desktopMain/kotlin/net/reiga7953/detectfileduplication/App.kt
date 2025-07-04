package net.reiga7953.detectfileduplication

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.persistentListOf

@Composable
@Preview
fun App() {
    val stateHolder = remember { MainViewStateHolder() }
    var dataTableContent = remember { mutableStateListOf<ItemData>() }
    var dialogContent = "Title" to "Content"

    MaterialTheme {
        Column(modifier = Modifier.padding(40.dp)) {
            GridView(
                persistentListOf(
                    { TextField(
                        value = stateHolder.folderPath,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Folder Path ") },
                        isError = stateHolder.folderPath.isEmpty(),
                        onValueChange = {
                            stateHolder.folderPath = it
                        },
                        singleLine = true
                    ) },
                    { FilledTonalButton(
                        onClick = {},
                        content = { Text("Select") }
                    ) },
                    { TextField(
                        value = stateHolder.extFilter,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Extension filter (Optional)") },
                        placeholder = { Text("ex) .png, .jpg") },
                        onValueChange = {
                            stateHolder.extFilter = it
                        },
                        singleLine = true,
                    ) },
                    { FilledTonalButton(
                        onClick = {},
                        content = { Text("Select") }
                    ) }
                ))
            Button(
                onClick = {
                    detectFileDuplication(
                        stateHolder.folderPath,
                        stateHolder.extFilter.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                    ) {
                        stateHolder.detectingProgress = it
                        println("Progress: $it")
                    }.also {
                        when {
                            it.isErr -> {
                                stateHolder.open()
                                dialogContent = it.error.message to "Please check that the folder path you entered is correct."
                            }
                            it.isOk -> {
                                dataTableContent.clear()
                                dataTableContent.addAll(it.value.map { (key, value) ->
                                    ItemData(key, value)
                                })
                            }
                        }
                    }
                },
                enabled = stateHolder.folderPath.isNotEmpty(),
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                content = { Text("Detect") }
            )
            DataTable(content = dataTableContent)
            LinearProgressIndicator(
                progress = { stateHolder.detectingProgress },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (stateHolder.isDialogOpen) {
            AlertDialog(
                onDismissRequest = {},
                confirmButton = {
                    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp)) {
                        Button(
                            onClick = { stateHolder.close() },
                            modifier = Modifier.align(Alignment.End),
                            content = { Text("OK") }
                        )
                    }
                },
                title = { Text(dialogContent.first) },
                text = { Text(dialogContent.second) }
            )
        }
    }
}

@Stable
class MainViewStateHolder {
    var folderPath by mutableStateOf("")
        internal set
    var extFilter by mutableStateOf("")
        internal set
    var detectingProgress by mutableFloatStateOf(0.0F)
        internal set
    var isDialogOpen by mutableStateOf(false)
        private set

    fun open() {
        isDialogOpen = true
    }

    fun close() {
        isDialogOpen = false
    }
}
