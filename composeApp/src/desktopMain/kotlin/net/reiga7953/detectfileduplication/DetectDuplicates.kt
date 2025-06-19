package net.reiga7953.detectfileduplication

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import java.io.File
import java.nio.file.InvalidPathException
import java.nio.file.Path
import java.security.MessageDigest

fun detectFileDuplication(folderPathStr: String, extFilter: List<String>, onProgressChange: (Float) -> Unit): Result<Map<ByteArray, List<String>>, ErrCode> {
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
            ?.also { progStepValue = (0.95F - currentProg) / (it.size - 1)}
            ?.associateBy({ deriveFileHash(it, onProgressChange) }, { it.name })
            ?.let { map ->
                Ok(map.entries.groupBy { it.key }.mapValues { entry -> entry.value.map { it.value } })
                    .also { onProgressChange(1.0F) }
            } ?: Err(ErrCode.NO_FILES).also { onProgressChange(1.0F) }
    } catch (_: InvalidPathException) {
        Err(ErrCode.INVALID_PATH)
    } catch (_: SecurityException) {
        Err(ErrCode.PERM_DENIED)
    }
}

var currentProg = 0.10F
var progStepValue = 0.0F
val md: MessageDigest = MessageDigest.getInstance("SHA-256")

private fun deriveFileHash(file: File, progress: (Float) -> Unit): ByteArray {
    md.update(file.readBytes())
    val hash = md.digest()
    md.reset()
    currentProg + progStepValue
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
