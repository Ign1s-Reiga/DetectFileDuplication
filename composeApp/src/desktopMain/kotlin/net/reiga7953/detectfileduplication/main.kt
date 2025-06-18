package net.reiga7953.detectfileduplication

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Detect File Duplication",
    ) {
        App()
    }
}