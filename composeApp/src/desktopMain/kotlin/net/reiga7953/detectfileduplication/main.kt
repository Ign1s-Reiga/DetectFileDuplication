package net.reiga7953.detectfileduplication

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.awt.Dimension

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Detect File Duplication",
    ) {
        window.minimumSize = Dimension(800, 600)
        App()
    }
}
