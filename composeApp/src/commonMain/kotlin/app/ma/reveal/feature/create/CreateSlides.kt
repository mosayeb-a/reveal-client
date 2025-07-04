package app.ma.reveal.feature.create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.unit.dp
import app.ma.reveal.common.DeviceConfiguration
import app.ma.reveal.common.ui.Appbar
import app.ma.reveal.common.ui.EmptyStateFaces
import app.ma.reveal.common.ui.LoadingBox
import app.ma.reveal.common.ui.Message
import app.ma.reveal.common.ui.RevealWebView
import app.ma.reveal.feature.create.component.SlideContentEditor
import com.multiplatform.webview.web.WebViewNavigator
import com.multiplatform.webview.web.WebViewState

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CreateSlides(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onSavedClick: () -> Unit,
    viewState: CreateSlidesState,
    webViewState: WebViewState?,
    navigator: WebViewNavigator,
    onPreviousSlideClick: (navigator: WebViewNavigator) -> Unit,
    onNextSlideClick: (navigator: WebViewNavigator) -> Unit,
    onAddSlideClick: (content: String, navigator: WebViewNavigator, webViewState: WebViewState?) -> Unit,
    onDiscardPresentation: () -> Unit,
    deviceConfiguration: DeviceConfiguration
) {
    var showSaveDialog by remember { mutableStateOf(false) }
    var markdownContent by remember { mutableStateOf("") }

    BackHandler(enabled = true) {
        if (viewState.slides.isEmpty()) {
            onBack()
        } else {
            showSaveDialog = true
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Appbar {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "back"
                    )
                }
                Text(
                    text = viewState.presentationId ?: "",
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = onSavedClick) {
                    Icon(
                        imageVector = Icons.Rounded.Done,
                        contentDescription = "save"
                    )
                }
            }
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                if (viewState.slides.isNotEmpty() && webViewState != null) {
                    RevealWebView(
                        modifier = Modifier
//                            .padding(top = 56.dp)
                            .weight(1f)
                            .fillMaxWidth(),
                        state = webViewState,
                        navigator = navigator,
                        onError = {},
                        onPreviousClick = { onPreviousSlideClick(navigator) },
                        onNextClick = { onNextSlideClick(navigator) },
                        showSlideNavigation = false,
                        onCreated = {}
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.secondary),
                        contentAlignment = Alignment.Center
                    ) {
                        Message(
                            modifier = Modifier,
                            message = "No slides added yet. Use the + button to add a slide.",
                            faces = EmptyStateFaces.suggestion
                        )
                    }
                }

                HorizontalDivider(
                    thickness = 1.dp,
                    color = Black.copy(alpha = 0.13f)
                )

                SlideContentEditor(
                    value = markdownContent,
                    onValueChange = { markdownContent = it },
                    onActionClick = {
                        onAddSlideClick(markdownContent, navigator, webViewState)
                        markdownContent = ""
                    }
                )
            }

            if (viewState.isLoading) {
                LoadingBox()
            }

            if (showSaveDialog) {
                AlertDialog(
                    onDismissRequest = { showSaveDialog = false },
                    containerColor = MaterialTheme.colorScheme.surface,
                    title = { Text("Save Presentation") },
                    text = { Text("Do you want to save your presentation before exiting?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showSaveDialog = false
                                onSavedClick()
                            },
                            enabled = viewState.slides.isNotEmpty(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Text("Save")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showSaveDialog = false
                                onDiscardPresentation()
                                onBack()
                            },
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Text("Discard")
                        }
                    }
                )
            }
        }
    }
}
