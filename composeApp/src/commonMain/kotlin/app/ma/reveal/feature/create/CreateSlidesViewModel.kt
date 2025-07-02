package app.ma.reveal.feature.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.ma.reveal.common.HTML_TEMPLATE
import app.ma.reveal.common.REVEAL_DIR
import app.ma.reveal.common.SLIDES_DIR
import app.ma.reveal.common.getBaseAssetPath
import app.ma.reveal.domain.Slide
import app.ma.reveal.domain.usecase.GoToNextSlide
import app.ma.reveal.domain.usecase.GoToPreviousSlide
import app.ma.reveal.domain.usecase.GoToSlide
import app.ma.reveal.domain.usecase.SavePresentation
import com.multiplatform.webview.web.WebContent
import com.multiplatform.webview.web.WebViewNavigator
import com.multiplatform.webview.web.WebViewState
import io.github.aakira.napier.Napier
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.createDirectories
import io.github.vinceglb.filekit.delete
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.writeString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.io.files.Path
import reveal.composeapp.generated.resources.Res
import java.util.UUID

data class CreateSlidesState(
    val isLoading: Boolean = false,
    val slides: List<Slide> = emptyList(),
    val presentationId: String? = null,
    val htmlFilePath: String? = null
)

class CreateSlidesViewModel(
    private val savePresentation: SavePresentation,
    private val goToNextSlide: GoToNextSlide,
    private val goToPreviousSlide: GoToPreviousSlide,
    private val goToSlide: GoToSlide
) : ViewModel() {
    private val _state = MutableStateFlow(CreateSlidesState())
    val state = _state.asStateFlow()

    private val _webViewState = MutableStateFlow<WebViewState?>(null)
    val webViewState = _webViewState.asStateFlow()

    private val _navigator = MutableStateFlow(WebViewNavigator(viewModelScope))
    val navigator = _navigator.asStateFlow()

    init {
        val htmlFile = PlatformFile(
            Path(FileKit.filesDir.absolutePath(), "$REVEAL_DIR/$SLIDES_DIR")
        ) / "${UUID.randomUUID()}.html"
        val initialUrl = if (htmlFile.exists()) "file://${htmlFile.absolutePath()}" else ""
        _webViewState.value = WebViewState(WebContent.Url(initialUrl))
    }

    private fun generateHtmlContent(slides: List<Slide>, presentationId: String): String {
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
        return HTML_TEMPLATE.format(
            "New Presentation",
            assetBasePath, assetBasePath, assetBasePath, assetBasePath,
            slidesHtml,
            assetBasePath, assetBasePath, assetBasePath, assetBasePath, assetBasePath
        ).also {
            try {
                Res.getUri("files/reveal/dist/reveal.js")
            } catch (e: Exception) {
                Napier.e("reveal.js asset not found: ${e.message}", e)
            }
        }
    }

    fun addSlide(content: String, navigator: WebViewNavigator, webViewState: WebViewState) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                _state.update { it.copy(isLoading = true) }
                val presentationId = _state.value.presentationId ?: UUID.randomUUID().toString()
                val newSlide = Slide(content, _state.value.slides.size)
                val updatedSlides = _state.value.slides + newSlide
                val htmlContent = generateHtmlContent(updatedSlides, presentationId)
                val htmlFile = saveHtmlFile(htmlContent, presentationId)
                val filePath = "file://${htmlFile.absolutePath()}"

                _state.update {
                    it.copy(
                        slides = updatedSlides,
                        presentationId = presentationId,
                        htmlFilePath = filePath
                    )
                }

                _state.value.htmlFilePath?.let { path ->
                    webViewState.content = WebContent.Url(path)
                    viewModelScope.launch(Dispatchers.Main) {
                        navigator.evaluateJavaScript("window.location.reload(true);")
                        goToSlide(navigator = navigator, slidesCount = _state.value.slides.size)
                        _state.update { it.copy(isLoading = false) }
                    }
                } ?: run {
                    Napier.e("failed to load html file: path is null")
                    _state.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                Napier.e("failed to add slide: ${e.message}", e)
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private suspend fun saveHtmlFile(htmlContent: String, presentationId: String): PlatformFile {
        val slidesDir = PlatformFile(Path(FileKit.filesDir.absolutePath(), "$REVEAL_DIR/$SLIDES_DIR"))
        slidesDir.createDirectories()
        val htmlFile = slidesDir / "$presentationId.html"
        htmlFile.writeString(htmlContent)
        return htmlFile.also {
            if (!it.exists()) Napier.e("html file does not exist at: ${it.absolutePath()}")
        }
    }

    fun savePresentation(onSuccess: (presentationId: String) -> Unit) {
        viewModelScope.launch(Dispatchers.Default) {
            if (_state.value.slides.isEmpty()) {
                return@launch
            }
            try {
                _state.update { it.copy(isLoading = true) }
                val presentationId = _state.value.presentationId ?: UUID.randomUUID().toString()
                savePresentation(
                    presentationId = presentationId,
                    slides = _state.value.slides,
                    title = presentationId
                ).fold(
                    onSuccess = { path ->
                        viewModelScope.launch(Dispatchers.Main) {
                            _state.update { it.copy(isLoading = false) }
                            onSuccess(presentationId)
                        }
                    },
                    onFailure = { error ->
                        Napier.e("failed to save presentation: ${error.message}", error)
                        _state.update { it.copy(isLoading = false) }
                    }
                )
            } catch (e: Exception) {
                Napier.e("error saving presentation: ${e.message}", e)
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun discardPresentation() {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                _state.update { it.copy(isLoading = true) }
                val slidesDir = PlatformFile(Path(FileKit.filesDir.absolutePath(), "$REVEAL_DIR/$SLIDES_DIR"))
                if (slidesDir.exists()) {
                    val htmlFile = slidesDir / "${_state.value.presentationId}.html"
                    htmlFile.delete(mustExist = false)
                }
                _state.update { CreateSlidesState(isLoading = false) }
                _webViewState.update { WebViewState(WebContent.Url("")) }
            } catch (e: Exception) {
                Napier.e("failed to discard presentation: ${e.message}", e)
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun previousSlide(navigator: WebViewNavigator) {
        viewModelScope.launch(Dispatchers.Main) {
            goToPreviousSlide(navigator)
        }
    }

    fun nextSlide(navigator: WebViewNavigator) {
        viewModelScope.launch(Dispatchers.Main) {
            goToNextSlide(navigator)
        }
    }
}