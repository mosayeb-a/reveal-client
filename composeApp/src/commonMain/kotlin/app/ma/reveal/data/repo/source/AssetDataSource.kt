package app.ma.reveal.data.repo.source

import app.ma.reveal.common.getBaseAssetPath
import io.github.aakira.napier.Napier
import reveal.composeapp.generated.resources.Res

class AssetDataSource {

    private val assetPaths = listOf(
        "files/reveal/slides/auto-animate.html",
        "files/reveal/slides/layout-helpers.html",
        "files/reveal/slides/markdown.html",
        "files/reveal/slides/math.html"
    )

    fun listAssetPresentations(): Result<List<String>> {
        return try {
            val basePath = getBaseAssetPath()
            val fullPaths = assetPaths.map { path ->
                if (basePath.startsWith("file:///android_asset/"))
                    "file:///android_asset/composeResources/reveal.composeapp.generated.resources/$path"
                else
                    "http://127.0.0.1:8080/$path"
            }
            Result.success(fullPaths)
        } catch (e: Exception) {
            Napier.e("failed to list asset presentations: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun readAssetPresentation(path: String): Result<String> {
        return try {
            val resourcePath = when {
                path.startsWith("file:///android_asset/composeResources/reveal.composeapp.generated.resources/") ->
                    path.removePrefix("file:///android_asset/composeResources/reveal.composeapp.generated.resources/")
                path.startsWith("http://127.0.0.1:8080/") ->
                    path.removePrefix("http://127.0.0.1:8080/")
                else -> path
            }
            val content = Res.readBytes(resourcePath).decodeToString()
            Result.success(content)
        } catch (e: Exception) {
            Napier.e("failed to read asset presentation: ${e.message}", e)
            Result.failure(e)
        }
    }
}