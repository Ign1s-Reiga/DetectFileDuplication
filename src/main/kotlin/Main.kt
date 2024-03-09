
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.material.TextField
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
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.io.File
import java.nio.file.InvalidPathException
import java.nio.file.Path

@Composable
@Preview
fun App() {
    val mainViewState = remember { MainViewStateHolder() }
    var dialogContent = "Title" to "Content"

    val regex = Regex("")

    MaterialTheme {
        Box(modifier = Modifier.padding(horizontal = 20.dp).offset(y = 55.dp)) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Folder path: ")
                    Spacer(modifier = Modifier.width(7.dp))
                    TextField(
                        value = mainViewState.folderPath,
                        modifier = Modifier.fillMaxWidth(),
                        isError = mainViewState.folderPath.isEmpty(),
                        onValueChange = {
                            mainViewState.folderPath = it
                        },
                        singleLine = true
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Extension filter (Optional): ", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(7.dp))
                    TextField(
                        value = mainViewState.extFilter,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("ex) .png, .jpg") },
                        onValueChange = {
                            mainViewState.extFilter = it
                        },
                        singleLine = true,
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        detectFileDuplication(mainViewState.folderPath)
                                .also {
                                    when(it) {
                                        Result.NOT_EXISTS -> {
                                            mainViewState.open()
                                            dialogContent = "Such folder was not found" to "Please check folder path what you typed is correctly."
                                        }
                                        Result.INVALID_PATH -> {
                                            mainViewState.open()
                                            dialogContent = "Invalid folder path" to "Please check folder path what you typed is correctly."
                                        }
                                        else -> {}
                                    }
                                }
                    },
                    enabled = mainViewState.folderPath.isNotEmpty(),
                    modifier = Modifier.align(Alignment.End),
                    content = { Text("Detect") }
                )
                Tab(
                    selected = false,
                    onClick = {},
                )

                LinearProgressIndicator(
                    progress = mainViewState.detectingProgress,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        if (mainViewState.isDialogOpen) {
            AlertDialog(
                onDismissRequest = {},
                buttons = {
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

fun main() = application {
    Window(
        title = "Detect File Duplication",
        onCloseRequest = ::exitApplication
    ) {
        App()
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
