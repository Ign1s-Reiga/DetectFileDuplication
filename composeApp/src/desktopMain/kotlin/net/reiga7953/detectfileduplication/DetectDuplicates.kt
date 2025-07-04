package net.reiga7953.detectfileduplication

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import java.io.File
import java.nio.file.InvalidPathException
import java.nio.file.Path
import java.security.MessageDigest
import kotlin.math.round
import kotlin.properties.Delegates

var currentProg = 0.10F
var progStepValue by Delegates.notNull<Float>()
val md: MessageDigest = MessageDigest.getInstance("SHA-256")

fun detectFileDuplication(folderPathStr: String, extFilter: List<String>, onProgressChange: (Float) -> Unit): Result<Map<String, List<String>>, ErrCode> {
    return try {
        val path = Path.of(folderPathStr)
        val folder = path.toFile()
        onProgressChange(0.05F)

        if (!folder.exists())
            Err(ErrCode.NOT_EXIST)
        if (!folder.isDirectory)
            Err(ErrCode.NOT_DIR)
        onProgressChange(0.1F)

        // TODO: Add option to collect files recursively
        folder.listFiles { it.isFile }
            .filter { if (extFilter.isEmpty()) true else extFilter.contains(it.extension) }
            .takeIf { it.isNotEmpty() }
            ?.also {
                progStepValue = round((0.90F - currentProg) / it.size * 1000) / 1000
                println("Total files: ${it.size}, Step value: $progStepValue")
            }
            ?.associateBy({ deriveFileHash(it, onProgressChange)}, { it.name })
            ?.let { map ->
                Ok(map.entries.groupBy({ it.key.joinToString("") { byte -> "%02x".format(byte) } }, { it.value }))
                    .also { onProgressChange(1.0F) }
            } ?: Err(ErrCode.NO_FILES).also { onProgressChange(1.0F) }
    } catch (_: InvalidPathException) {
        Err(ErrCode.INVALID_PATH)
    } catch (_: SecurityException) {
        Err(ErrCode.PERM_DENIED)
    }
}

private fun deriveFileHash(file: File, progress: (Float) -> Unit): ByteArray {
    println("File: ${file.name}, Size: ${file.length()} bytes")
    md.update(file.readBytes())
    val hash = md.digest()
    md.reset()
    currentProg += progStepValue
    currentProg = round(currentProg * 1000) / 1000
    progress(currentProg)
    return hash
}

enum class ErrCode(val message: String) {
    INVALID_PATH("Invalid Path format"),
    NOT_EXIST("This Path does not exist"),
    NOT_DIR("The Path is not a directory"),
    NO_FILES("No files found in the directory"),
    PERM_DENIED("Security exception occurred")
}
