package net.reiga7953.detectfileduplication

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.vinceglb.filekit.FileKit
import java.awt.Dimension

fun main() = application {
    FileKit.init("DetectFileDuplication")

    Window(
        onCloseRequest = ::exitApplication,
        title = "Detect File Duplication",
    ) {
        window.minimumSize = Dimension(800, 600)
        App(this.window)
    }
}
