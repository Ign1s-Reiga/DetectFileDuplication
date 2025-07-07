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
import kotlinx.coroutines.launch
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
                            AppCoroutineScope().launch(Dispatchers.Unconfined) {
                                stateHolder.folderPath = FileKit.openDirectoryPicker(
                                    "Select Folder",
                                    PlatformFile(File(System.getProperty("user.dir"))),
                                    dialogSettings(ownerWindow)
                                )?.path ?: ""
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
                                    stateHolder.openErrDialog()
                                    dialogContent = it.error.message to "Please check that the folder path you typed is correct."
                                }
                                it.isOk -> {
                                    dataTableContent.updateTo(it.value.map { (key, value) ->
                                        ItemData(key, value.toMutableList())
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
                    onClick = { stateHolder.isConfirmDialogOpen = true },
                    enabled = dataTableContent.isNotEmpty(),
                    modifier = Modifier.weight(1F),
                    content = { Text("Delete All Duplicates") }
                )
            }
            DataTable(content = dataTableContent)
            LinearProgressIndicator(
                progress = { stateHolder.detectingProgress },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (stateHolder.isErrDialogOpen) {
            AlertDialog(
                onDismissRequest = {},
                confirmButton = {
                    Button(
                        onClick = { stateHolder.closeErrDialog() },
                        content = { Text("OK") }
                    )
                },
                title = { Text(dialogContent.first) },
                text = { Text(dialogContent.second) }
            )
        }
        ConfirmationDialog(
            visible = stateHolder.isConfirmDialogOpen,
            onConfirm = {
                stateHolder.isConfirmDialogOpen = false
                // TODO: Popup toast when finished deleting files.
                // TODO: Handle deleteFile Result
                // TODO: Add ignore word list setting, ignore files that contains those words when sorting.
                dataTableContent.map {
                    if (it.files.size > 1) {
                        val sorted = it.files.sortedBy { path -> path.toString() }.drop(1)
                        sorted.forEach { path -> deleteFile(path) }
                            .let { _ -> it.removePaths(sorted) }
                    } else {
                        it
                    }
                }.also {
                    dataTableContent.updateTo(it)
                }
            },
            onDismiss = { stateHolder.isConfirmDialogOpen = false },
            text = { Text("Are you sure you want to delete all duplicate files? This action cannot be undone.") }
        )
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
    var isErrDialogOpen by mutableStateOf(false)
        private set
    var isConfirmDialogOpen by mutableStateOf(false)
        internal set

    fun openErrDialog() {
        isErrDialogOpen = true
    }

    fun closeErrDialog() {
        isErrDialogOpen = false
    }
}

fun <T> MutableList<T>.updateTo(newList: List<T>) {
    clear()
    addAll(newList)
}
