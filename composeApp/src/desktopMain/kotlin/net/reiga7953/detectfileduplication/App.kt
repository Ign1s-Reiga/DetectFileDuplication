package net.reiga7953.detectfileduplication

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings
import io.github.vinceglb.filekit.dialogs.openDirectoryPicker
import io.github.vinceglb.filekit.path
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.awt.Window
import java.io.File

@Composable
@Preview
fun App(ownerWindow: Window) {
    val stateHolder = remember { MainViewStateHolder() }
    val dataTableContent = remember { mutableStateListOf<ItemData>() }
    var dialogContent = "Title" to "Content"

    val dialogSettings = { owner: Window -> FileKitDialogSettings(owner) }

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
                        onClick = {
                            runBlocking {
                                stateHolder.folderPath = openDirectoryPicker(dialogSettings(ownerWindow))
                            }
                        },
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
                    ) }
                ))
            Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
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
                                    dialogContent = it.error.message to "Please check that the folder path you typed is correct."
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
                    modifier = Modifier.weight(1F),
                    content = { Text("Detect") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {},
                    enabled = dataTableContent.isNotEmpty(),
                    modifier = Modifier.weight(1F),
                    content = { Text("Delete Duplicates") }
                )
            }
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

suspend fun openDirectoryPicker(dialogSettings: FileKitDialogSettings): String = coroutineScope {
    withContext(Dispatchers.Unconfined) {
        FileKit.openDirectoryPicker(
            "Select Folder",
            PlatformFile(File(System.getProperty("user.dir"))),
            dialogSettings
        )?.path ?: ""
    }
}
