package app.ma.reveal.feature.present

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.ma.reveal.common.DeviceConfiguration
import app.ma.reveal.common.ui.EmptyStateFaces
import app.ma.reveal.common.ui.LoadingBox
import app.ma.reveal.common.ui.Message
import app.ma.reveal.common.ui.RevealWebView
import com.multiplatform.webview.web.WebViewNavigator
import com.multiplatform.webview.web.WebViewState
import io.github.aakira.napier.Napier

@Composable
fun Present(
    modifier: Modifier = Modifier,
    webViewSate: WebViewState,
    navigator: WebViewNavigator,
    onPreviousSlideClicked: () -> Unit,
    deviceConfiguration: DeviceConfiguration,
    onNextSlideClicked: () -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentColor = MaterialTheme.colorScheme.onPrimary,
        containerColor = MaterialTheme.colorScheme.secondary,
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
                onCreated = {},
                onPreviousClick = onPreviousSlideClicked,
                onNextClick = onNextSlideClicked,
                showSlideNavigation = deviceConfiguration !in listOf(
                    DeviceConfiguration.TABLET_PORTRAIT,
                    DeviceConfiguration.TABLET_LANDSCAPE,
                    DeviceConfiguration.DESKTOP
                )
            )

            if (webViewSate.isLoading) {
                LoadingBox()
            }
        }
    }
}