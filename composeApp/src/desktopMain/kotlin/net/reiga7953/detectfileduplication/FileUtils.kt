package net.reiga7953.detectfileduplication

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import java.io.IOException
import java.lang.Exception
import java.nio.file.Path
import kotlin.io.path.deleteIfExists

// TODO: Handle Result values
fun deleteFile(filePath: Path): Result<Unit, Exception> = when (filePath.deleteIfExists()) {
    true -> Ok(Unit)
    false -> Err(IOException("Failed to delete file: $filePath"))
}
