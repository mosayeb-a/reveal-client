package app.ma.reveal.feature.present

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.ma.reveal.common.getBaseAssetPath
import app.ma.reveal.domain.usecase.GoToNextSlide
import app.ma.reveal.domain.usecase.GoToPreviousSlide
import app.ma.reveal.domain.usecase.GoToSlide
import com.multiplatform.webview.web.WebContent
import com.multiplatform.webview.web.WebViewNavigator
import com.multiplatform.webview.web.WebViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PresentViewModel(
    private val goToNextSlide: GoToNextSlide,
    private val goToPreviousSlide: GoToPreviousSlide,
    private val goToSlide: GoToSlide,
    path: String,
) : ViewModel() {
    private val _webViewState = MutableStateFlow<WebViewState?>(null)
    val webViewState = _webViewState.asStateFlow()

    private val _navigator = MutableStateFlow(WebViewNavigator(viewModelScope))
    val navigator = _navigator.asStateFlow()

    init {
        val normalizedPath = when {
            path.startsWith("file://") || path.startsWith("http://") -> path
            else -> "${getBaseAssetPath()}/slides/$path"
        }

        if (_webViewState.value?.lastLoadedUrl != normalizedPath) {
            _webViewState.value = WebViewState(
                webContent = WebContent.Url(url = path)
            )
        }
    }


    fun previousSlide(navigator: WebViewNavigator) {
        viewModelScope.launch {
            goToPreviousSlide(navigator)
        }
    }

    fun nextSlide(navigator: WebViewNavigator) {
        viewModelScope.launch {
            goToNextSlide(navigator)
        }
    }
}