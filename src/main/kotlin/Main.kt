
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
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.nio.file.InvalidPathException
import java.nio.file.Path

@Composable
@Preview
fun App() {
    var folderPath by remember { mutableStateOf("") }
    var filterExt by remember { mutableStateOf("") }
    var detectingProgress by remember { mutableStateOf(0.0F) }

    val regex = Regex("")

    MaterialTheme {
        Box(modifier = Modifier.padding(horizontal = 20.dp).offset(y = 55.dp)) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Folder path: ")
                    Spacer(modifier = Modifier.width(7.dp))
                    TextField(
                        value = folderPath,
                        modifier = Modifier.fillMaxWidth(),
                        isError = folderPath.isEmpty(),
                        onValueChange = {
                            folderPath = it
                        },
                        singleLine = true
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Extension filter (Optional): ", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(7.dp))
                    TextField(
                        value = filterExt,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("ex) .png, .jpg") },
                        onValueChange = {
                            filterExt = it
                        },
                        singleLine = true,
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        detectFileDuplication(folderPath)
                                .also {

                                }
                    },
                    enabled = folderPath.isNotEmpty(),
                    modifier = Modifier.align(Alignment.End),
                    content = { Text("Detect") }
                )

                LinearProgressIndicator(
                    progress = detectingProgress,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

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

private fun detectFileDuplication(folderPath: String): Result {
    try {
        val path = Path.of(folderPath)
    } catch (e: InvalidPathException) {
        e.printStackTrace()
        return Result.INVALID_PATH
    }

}

enum class Result {
    INVALID_PATH,
}
