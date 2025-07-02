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

@Composable
fun Present(
    modifier: Modifier = Modifier,
    viewModel: PresentViewModel
) {
    val webViewSate by viewModel.webViewState.collectAsStateWithLifecycle()
    val navigator by viewModel.navigator.collectAsStateWithLifecycle()

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
                state = webViewSate!!,
                navigator = navigator,
                onCreated = {

                },
                onPreviousClick = { viewModel.previousSlide(navigator) },
                onNextClick = { viewModel.nextSlide(navigator) },
                showSlideNavigation = true
            )

            if (webViewSate!!.isLoading) {
                LoadingBox()
            }
        }
    }
}