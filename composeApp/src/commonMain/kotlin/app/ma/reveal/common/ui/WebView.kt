package app.ma.reveal.common.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.ma.reveal.common.BOTTOM_BAR_HEIGHT
import app.ma.reveal.common.configureWebView
import app.ma.reveal.common.kcefSetup
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.WebViewNavigator
import com.multiplatform.webview.web.WebViewState
import com.multiplatform.webview.web.rememberWebViewNavigator
import io.github.aakira.napier.Napier

@Composable
fun RevealWebView(
    modifier: Modifier = Modifier,
    state: WebViewState,
    navigator: WebViewNavigator = rememberWebViewNavigator(),
    onCreated: () -> Unit = {},
    onError: (String) -> Unit = {},
    onPreviousClick: () -> Unit = {},
    onNextClick: () -> Unit = {},
    showSlideNavigation: Boolean = true
) {
    var initialized by remember { mutableStateOf(false) }

    // todo: must be refactored, KCEF should be initialized only once
    LaunchedEffect(Unit) {
        kcefSetup(
            onInitialized = { initialized = true },
            onError = { error ->
                error?.printStackTrace()
                onError(error?.message ?: "Unknown error during WebView initialization")
            }
        )
    }

    DisposableEffect(state) {
        state.webSettings.apply {
            isJavaScriptEnabled = true
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true
            androidWebSettings.apply {
                supportZoom = false
                safeBrowsingEnabled = true
                allowFileAccess = true
                domStorageEnabled = true
            }
        }
        onDispose { }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (initialized) {
            WebView(
                modifier = Modifier
                    .padding(bottom = if (showSlideNavigation) BOTTOM_BAR_HEIGHT else 0.dp)
                    .fillMaxSize(),
                state = state,
                navigator = navigator,
                onCreated = { browser ->
                    configureWebView(browser)
                    onCreated()
                    Napier.d("WebView created: $browser")
                },
                onDispose = { browser ->
                    Napier.d("WebView disposed: $browser")
                }
            )

            AnimatedVisibility(
                visible = showSlideNavigation,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(durationMillis = 300)
                ),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(durationMillis = 300)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                NavigationButtons(
                    modifier = Modifier.fillMaxWidth(),
                    onPreviousClick = onPreviousClick,
                    onNextClick = onNextClick
                )
            }
        }
    }
}