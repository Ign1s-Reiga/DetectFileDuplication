package net.reiga7953.detectfileduplication

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.collections.immutable.persistentListOf

import java.io.File
import java.nio.file.InvalidPathException
import java.nio.file.Path

@Composable
@Preview
fun App() {
    val mainViewState = remember { MainViewStateHolder() }
    var dialogContent = "Title" to "Content"

    val regex = Regex("")
    val testData = persistentListOf("Apple", "Orange", "Grape")

    MaterialTheme {
        Column(modifier = Modifier.padding(40.dp)) {
            GridView(
                persistentListOf(
                    { Text("Folder path: ") },
                    { TextField(
                        value = mainViewState.folderPath,
                        modifier = Modifier.fillMaxWidth(),
                        isError = mainViewState.folderPath.isEmpty(),
                        onValueChange = {
                            mainViewState.folderPath = it
                        },
                        singleLine = true
                    ) },
                    { Text("Extension filter (Optional): ", fontSize = 16.sp) },
                    { TextField(
                        value = mainViewState.extFilter,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("ex) .png, .jpg") },
                        onValueChange = {
                            mainViewState.extFilter = it
                        },
                        singleLine = true,
                    ) },
                    { Button(
                        onClick = {
                            detectFileDuplication(mainViewState.folderPath).also {
                                when (it) {
                                    Result.NOT_EXISTS -> {
                                        mainViewState.open()
                                        dialogContent =
                                            "Such folder was not found" to "Please check folder path what you typed is correctly."
                                    }
                                    Result.INVALID_PATH -> {
                                        mainViewState.open()
                                        dialogContent =
                                            "Invalid folder path" to "Please check folder path what you typed is correctly."
                                    }
                                    else -> {}
                                }
                            }
                        },
                        enabled = mainViewState.folderPath.isNotEmpty(),
                        modifier = Modifier.align(Alignment.End),
                        content = { Text("Detect") }
                    ) }
                ))
            DataTable(content = testData)
            LinearProgressIndicator(
                progress = { mainViewState.detectingProgress },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (mainViewState.isDialogOpen) {
            AlertDialog(
                onDismissRequest = {},
                confirmButton = {
                    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp)) {
                        Button(
                            onClick = { mainViewState.close() },
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

private fun detectFileDuplication(folderPathStr: String): Result {
    val fileList = mutableListOf<File>()

    try {
        val path = Path.of(folderPathStr)
        val folder = path.toFile()

        if (!folder.exists())
            return Result.NOT_EXISTS
    } catch (e: InvalidPathException) {
        e.printStackTrace()
        return Result.INVALID_PATH
    }
    return Result.SUCCESS
}

enum class Result {
    NOT_EXISTS,
    INVALID_PATH,
    SUCCESS
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
