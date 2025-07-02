package app.ma.reveal.feature.present

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.ma.reveal.common.ui.LoadingBox
import app.ma.reveal.common.ui.RevealWebView
import com.multiplatform.webview.web.WebViewNavigator
import com.multiplatform.webview.web.WebViewState

@Composable
fun Present(
    modifier: Modifier = Modifier,
    webViewSate: WebViewState,
    navigator: WebViewNavigator,
    onPreviousSlideClicked: () -> Unit,
    onNextSlideClicked: () -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            RevealWebView(
                modifier = Modifier.fillMaxSize(),
                state = webViewSate,
                navigator = navigator,
                onCreated = {

                },
                onPreviousClick = onPreviousSlideClicked,
                onNextClick = onNextSlideClicked,
                showSlideNavigation = true
            )

            if (webViewSate.isLoading) {
                LoadingBox()
            }
        }
    }
}