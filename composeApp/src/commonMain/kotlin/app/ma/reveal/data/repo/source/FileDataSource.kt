package app.ma.reveal.data.repo.source

import io.github.vinceglb.filekit.filesDir
import app.ma.reveal.common.REVEAL_DIR
import app.ma.reveal.common.SLIDES_DIR
import io.github.aakira.napier.Napier
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.createDirectories
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.list
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readString
import io.github.vinceglb.filekit.writeString
import java.io.File
import java.io.IOException

class FileDataSource {
    suspend fun savePresentation(presentationId: String, content: String): Result<String> {
        return try {
            val slidesDir = FileKit.filesDir / REVEAL_DIR / SLIDES_DIR
            slidesDir.createDirectories()
            val file = slidesDir / "$presentationId.html"
            file.writeString(content)
            Result.success("file://${file.absolutePath()}")
        } catch (e: IOException) {
            Napier.e("error saving presentation: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun listFilePresentations(): Result<List<PlatformFile>> {
        return try {
            val slidesDir = FileKit.filesDir / REVEAL_DIR / SLIDES_DIR
            if (!slidesDir.exists()) slidesDir.createDirectories()
            val files = slidesDir.list().filter { it.name.endsWith(".html") }
            Result.success(files)
        } catch (e: IOException) {
            Napier.e("error listing presentations: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun readFilePresentation(file: PlatformFile): Result<String> {
        return try {
            val content = file.readString()
            Result.success(content)
        } catch (e: IOException) {
            Napier.e("error reading presentation: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun readFilePresentationById(presentationId: String): Result<Pair<PlatformFile, String>> {
        return try {
            val slidesDir = FileKit.filesDir / REVEAL_DIR / SLIDES_DIR
            if (!slidesDir.exists()) slidesDir.createDirectories()
            val file = slidesDir / "$presentationId.html"
            if (!file.exists()) {
                return Result.failure(IOException("File $presentationId.html does not exist"))
            }
            val content = file.readString()
            Result.success(file to content)
        } catch (e: IOException) {
            Napier.e("error reading presentation by id $presentationId: ${e.message}", e)
            Result.failure(e)
        }
    }

    fun getFileLastModified(file: PlatformFile): Result<Long> {
        return try {
            val lastModified = File(file.absolutePath()).lastModified()
            Result.success(lastModified)
        } catch (e: IOException) {
            Napier.e("error getting last modified: ${e.message}", e)
            Result.failure(e)
        }
    }
}