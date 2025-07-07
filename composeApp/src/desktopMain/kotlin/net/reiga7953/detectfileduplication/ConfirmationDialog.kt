package net.reiga7953.detectfileduplication

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
@Preview
fun ConfirmationDialog(
    visible: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    text: @Composable () -> Unit
) {
    if (!visible) return
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {
            Button(
                onClick = { onConfirm() },
                content = { Text("OK") }
            )
        },
        dismissButton = {
            Button(
                onClick = { onDismiss() },
                content = { Text("Cancel") }
            )
        },
        title = { Text("Confirmation") },
        text = { text() }
    )
}
