package app.ma.reveal.data.repo

import app.ma.reveal.common.HTML_TEMPLATE
import app.ma.reveal.common.getBaseAssetPath
import app.ma.reveal.data.repo.source.AssetDataSource
import app.ma.reveal.data.repo.source.FileDataSource
import app.ma.reveal.domain.Presentation
import app.ma.reveal.domain.Slide
import app.ma.reveal.domain.repo.PresentationRepository
import io.github.aakira.napier.Napier
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.name
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class PresentationRepositoryImpl(
    private val assetDataSource: AssetDataSource,
    private val fileDataSource: FileDataSource
) : PresentationRepository {

    override suspend fun save(
        presentationId: String,
        slides: List<Slide>,
        title: String
    ): Result<String> {
        val slidesHtml = slides.mapIndexed { index, slide ->
            """
            <section data-markdown id="slide-$index">
                <textarea data-template>
                    ${slide.content.trim()}
                </textarea>
            </section>
            """.trimIndent()
        }.joinToString("\n")

        val assetBasePath = getBaseAssetPath()
        val htmlTemplate = HTML_TEMPLATE.format(
            title,
            assetBasePath, assetBasePath, assetBasePath, assetBasePath,
            slidesHtml,
            assetBasePath, assetBasePath, assetBasePath, assetBasePath, assetBasePath
        )

        return fileDataSource.savePresentation(presentationId, htmlTemplate).also { result ->
            result.onFailure { e ->
                Napier.e("failed to save presentation: ${e.message}", e)
            }
        }
    }

    override suspend fun get(
        id: String,
        fromAsset: Boolean,
        fromFiles: Boolean
    ): Result<Presentation> {
        try {
            if (fromAsset) {
                val assetPath = "files/reveal/slides/$id.html"
                val assetResult = assetDataSource.readAssetPresentation(assetPath)
                if (assetResult.isSuccess) {
                    val content = assetResult.getOrNull()
                        ?: return Result.failure(Exception("Asset content is null"))
                    return Result.success(
                        Presentation(
                            id = id,
                            name = extractTitle(content, id),
                            path = assetPath,
                            slideCount = countSlides(content),
                            addedDate = Clock.System.now(),
                            isAsset = true
                        )
                    )
                }
            }

            if (fromFiles) {
                val fileResult = fileDataSource.readFilePresentationById(id)
                if (fileResult.isSuccess) {
                    val (file, content) = fileResult.getOrNull() ?: return Result.failure(
                        Exception(
                            "File content is null"
                        )
                    )
                    val addedDate = fileDataSource.getFileLastModified(file).getOrNull()
                        ?: Clock.System.now().toEpochMilliseconds()
                    return Result.success(
                        Presentation(
                            id = id,
                            name = extractTitle(content, id),
                            path = "file://${file.absolutePath()}",
                            slideCount = countSlides(content),
                            addedDate = Instant.fromEpochMilliseconds(addedDate),
                            isAsset = false
                        )
                    )
                }
            }

            return Result.failure(Exception("Presentation with id $id not found"))
        } catch (e: Exception) {
            Napier.e("error in get presentation $id: ${e.message}", e)
            return Result.failure(e)
        }
    }

    override suspend fun getAllFrom(
        asset: Boolean,
        files: Boolean
    ): Result<List<Presentation>> {
        if (!asset && !files) {
            return Result.failure(Exception("At least one source (assets or files) must be enabled"))
        }

        val presentations = mutableListOf<Presentation>()
        try {
            if (asset) {
                assetDataSource.listAssetPresentations().fold(
                    onSuccess = { paths ->
                        paths.mapNotNull { path ->
                            assetDataSource.readAssetPresentation(path).getOrNull()
                                ?.let { content ->
                                    val id = path.substringAfterLast("/").removeSuffix(".html")
                                    Presentation(
                                        id = id,
                                        name = extractTitle(content, id),
                                        path = path,
                                        slideCount = countSlides(content),
                                        addedDate = Clock.System.now(),
                                        isAsset = true
                                    )
                                }
                        }.let { presentations.addAll(it) }
                    },
                    onFailure = { e ->
                        Napier.e("failed to load asset presentations: ${e.message}", e)
                        return Result.failure(e)
                    }
                )
            }

            if (files) {
                fileDataSource.listFilePresentations().fold(
                    onSuccess = { fileList ->
                        fileList.mapNotNull { file ->
                            fileDataSource.readFilePresentation(file).getOrNull()?.let { content ->
                                val addedDate = fileDataSource.getFileLastModified(file).getOrNull()
                                    ?: Clock.System.now().toEpochMilliseconds()
                                val id = file.name.removeSuffix(".html")
                                Presentation(
                                    id = id,
                                    name = extractTitle(content, id),
                                    path = "file://${file.absolutePath()}",
                                    slideCount = countSlides(content),
                                    addedDate = Instant.fromEpochMilliseconds(addedDate),
                                    isAsset = false
                                )
                            }
                        }.let { presentations.addAll(it) }
                    },
                    onFailure = { e ->
                        Napier.e("failed to load file presentations: ${e.message}", e)
                        return Result.failure(e)
                    }
                )
            }

            return Result.success(presentations.sortedByDescending { it.addedDate })
        } catch (e: Exception) {
            Napier.e("error in getAllFrom: ${e.message}", e)
            return Result.failure(e)
        }
    }

    private fun extractTitle(htmlContent: String, defaultName: String): String {
        val titleStart = htmlContent.indexOf("<title>")
        if (titleStart == -1) return defaultName
        val titleEnd = htmlContent.indexOf("</title>", titleStart)
        if (titleEnd == -1) return defaultName
        return htmlContent.substring(titleStart + 7, titleEnd).trim().ifBlank { defaultName }
    }

    private fun countSlides(htmlContent: String): Int = htmlContent.split("<section").size - 1
}