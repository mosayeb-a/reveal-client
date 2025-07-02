package app.ma.reveal.feature.create

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
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
import androidx.compose.ui.unit.dp
import app.ma.reveal.common.BOTTOM_BAR_HEIGHT
import app.ma.reveal.common.ui.Appbar
import app.ma.reveal.common.ui.EmptyStateFaces
import app.ma.reveal.common.ui.LoadingBox
import app.ma.reveal.common.ui.Message
import app.ma.reveal.common.ui.RevealWebView
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
    onDiscardPresentation: () -> Unit
) {
    var showAddSlideDialog by remember { mutableStateOf(false) }
    var slideContent by remember { mutableStateOf("") }
    var showSaveDialog by remember { mutableStateOf(false) }

    BackHandler(true) {
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
                IconButton(
                    onClick = onSavedClick
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Done,
                        contentDescription = "save"
                    )
                }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                modifier = Modifier.padding(
                    bottom = animateDpAsState(
                        targetValue = if (viewState.slides.count() > 1) BOTTOM_BAR_HEIGHT else 0.dp,
                        animationSpec = tween(durationMillis = 300)
                    ).value
                ),
                onClick = { showAddSlideDialog = true },
                icon = { Icon(Icons.Rounded.Add, contentDescription = "Add Slide") },
                text = { Text("Add Slide") },
                contentColor = MaterialTheme.colorScheme.onPrimary,
                containerColor = MaterialTheme.colorScheme.primary,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 3.dp
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (viewState.slides.isEmpty()) {
                Message(
                    message = "No slides added yet. Use the + button to add a slide.",
                    faces = EmptyStateFaces.suggestion
                )
            } else if (webViewState != null) {
                RevealWebView(
                    modifier = Modifier.fillMaxSize(),
                    state = webViewState,
                    navigator = navigator,
                    onError = { },
                    onPreviousClick = { onPreviousSlideClick(navigator) },
                    onNextClick = { onNextSlideClick(navigator) },
                    showSlideNavigation = viewState.slides.count() > 1,
                    onCreated = {}
                )
            }

            if (viewState.isLoading) {
                LoadingBox()
            }
        }

        if (showAddSlideDialog) {
            AlertDialog(
                onDismissRequest = { showAddSlideDialog = false },
                title = { Text("Add Slide") },
                text = {
                    androidx.compose.material3.OutlinedTextField(
                        value = slideContent,
                        onValueChange = { slideContent = it },
                        label = { Text("Markdown Content (use # for titles)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        maxLines = 10
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (slideContent.isNotBlank()) {
                                onAddSlideClick(slideContent, navigator, webViewState)
                                showAddSlideDialog = false
                                slideContent = ""
                            }
                        },
                        enabled = slideContent.isNotBlank()
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showAddSlideDialog = false
                        slideContent = ""
                    }) {
                        Text("Cancel")
                    }
                }
            )
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
                        enabled = viewState.slides.count() >= 1,
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