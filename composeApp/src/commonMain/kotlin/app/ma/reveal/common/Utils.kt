package app.ma.reveal.common

import androidx.compose.runtime.rememberCoroutineScope
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.multiplatform.webview.web.NativeWebView
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import java.text.SimpleDateFormat
import java.util.Locale

expect suspend fun kcefSetup(
    onInitialized: () -> Unit,
    onError: (Throwable?) -> Unit = {}
)

expect suspend fun onWebviewDisposed()

expect fun getBaseAssetPath(): String

fun formatDate(date: Instant, now: Instant = Instant.fromEpochMilliseconds(System.currentTimeMillis())): String {
    val dateLocal = date.toLocalDateTime(TimeZone.currentSystemDefault())
    val nowLocal = now.toLocalDateTime(TimeZone.currentSystemDefault())
    val daysBetween = (now - date).inWholeDays
    val pattern = when {
        dateLocal.date == nowLocal.date -> "HH:mm"
        daysBetween < 7 -> "EEE"
        dateLocal.month == nowLocal.month && dateLocal.year == nowLocal.year -> "MMM dd"
        else -> "dd.MM.yy"
    }
    return SimpleDateFormat(pattern, Locale.getDefault()).format(date.toEpochMilliseconds())
}

expect fun configureWebView(webView: NativeWebView)


enum class DeviceConfiguration {
    MOBILE_PORTRAIT,
    MOBILE_LANDSCAPE,
    TABLET_PORTRAIT,
    TABLET_LANDSCAPE,
    DESKTOP;

    companion object {
        fun fromWindowSizeClass(windowSizeClass: WindowSizeClass): DeviceConfiguration {
            val widthClass = windowSizeClass.windowWidthSizeClass
            val heightClass = windowSizeClass.windowHeightSizeClass

            return when {
                widthClass == WindowWidthSizeClass.COMPACT &&
                        heightClass == WindowHeightSizeClass.MEDIUM -> MOBILE_PORTRAIT
                widthClass == WindowWidthSizeClass.COMPACT &&
                        heightClass == WindowHeightSizeClass.EXPANDED -> MOBILE_PORTRAIT
                widthClass == WindowWidthSizeClass.EXPANDED &&
                        heightClass == WindowHeightSizeClass.COMPACT -> MOBILE_LANDSCAPE
                widthClass == WindowWidthSizeClass.MEDIUM &&
                        heightClass == WindowHeightSizeClass.EXPANDED -> TABLET_PORTRAIT
                widthClass == WindowWidthSizeClass.EXPANDED &&
                        heightClass == WindowHeightSizeClass.MEDIUM -> TABLET_LANDSCAPE
                else -> DESKTOP
            }
        }
    }
}

inline fun <reified T : Any> koinInject(): T {
    return object : KoinComponent {
        val value = get<T>()
    }.value
}

//package app.ma.reveal.feature.create
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.rounded.ArrowBack
//import androidx.compose.material.icons.rounded.Done
//import androidx.compose.material3.AlertDialog
//import androidx.compose.material3.Button
//import androidx.compose.material3.HorizontalDivider
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.ExperimentalComposeUiApi
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.backhandler.BackHandler
//import androidx.compose.ui.graphics.Color.Companion.Black
//import androidx.compose.ui.unit.dp
//import app.ma.reveal.common.DeviceConfiguration
//import app.ma.reveal.common.ui.Appbar
//import app.ma.reveal.common.ui.EmptyStateFaces
//import app.ma.reveal.common.ui.LoadingBox
//import app.ma.reveal.common.ui.Message
//import app.ma.reveal.common.ui.RevealWebView
//import app.ma.reveal.feature.create.component.SlideContentEditor
//import com.multiplatform.webview.web.WebViewNavigator
//import com.multiplatform.webview.web.WebViewState
//
//@OptIn(ExperimentalComposeUiApi::class)
//@Composable
//fun CreateSlides(
//    modifier: Modifier = Modifier,
//    onBack: () -> Unit,
//    onSavedClick: () -> Unit,
//    viewState: CreateSlidesState,
//    webViewState: WebViewState?,
//    navigator: WebViewNavigator,
//    onPreviousSlideClick: (navigator: WebViewNavigator) -> Unit,
//    onNextSlideClick: (navigator: WebViewNavigator) -> Unit,
//    onAddSlideClick: (content: String, navigator: WebViewNavigator, webViewState: WebViewState?) -> Unit,
//    onDiscardPresentation: () -> Unit,
//    deviceConfiguration: DeviceConfiguration
//) {
//    var showAddSlideDialog by remember { mutableStateOf(false) }
//    var slideContent by remember { mutableStateOf("") }
//    var showSaveDialog by remember { mutableStateOf(false) }
//    var markdownContent by remember { mutableStateOf("") }
//
//
//    BackHandler(true) {
//        if (viewState.slides.isEmpty()) {
//            onBack()
//        } else {
//            showSaveDialog = true
//        }
//    }
//
//    Scaffold(
//        modifier = modifier.fillMaxSize(),
//        topBar = {
//            Appbar {
//                IconButton(onClick = onBack) {
//                    Icon(
//                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
//                        contentDescription = "back"
//                    )
//                }
//                IconButton(
//                    onClick = onSavedClick
//                ) {
//                    Icon(
//                        imageVector = Icons.Rounded.Done,
//                        contentDescription = "save"
//                    )
//                }
//            }
//        },
//    ) { paddingValues ->
//        Box(
//            modifier = Modifier
//                .padding(paddingValues)
//                .fillMaxSize(),
//            contentAlignment = Alignment.Center
//        ) {
//            if (viewState.slides.isEmpty()) {
//                Message(
//                    modifier = modifier
//                        .background(MaterialTheme.colorScheme.secondary),
//                    message = "No slides added yet. Use the + button to add a slide.",
//                    faces = EmptyStateFaces.suggestion
//                )
//            }
//            Column(
//                modifier = Modifier
//            ) {
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize(),
//                    contentAlignment = Alignment.BottomCenter
//                ) {
//                    HorizontalDivider(
//                        thickness = 1.dp,
//                        color = Black.copy(alpha = 0.13f)
//                    )
//                    SlideContentEditor(
//                        value = markdownContent,
//                        onValueChange = {
//                            markdownContent = it
//                        },
//                        onActionClick = {
//                            onAddSlideClick(markdownContent, navigator, webViewState)
//                            markdownContent = ""
//                        }
//                    )
//                }
//                if (webViewState != null) {
//                    RevealWebView(
//                        modifier = Modifier.fillMaxSize(),
//                        state = webViewState,
//                        navigator = navigator,
//                        onError = { },
//                        onPreviousClick = { onPreviousSlideClick(navigator) },
//                        onNextClick = { onNextSlideClick(navigator) },
//                        showSlideNavigation = false,
//                        onCreated = {}
//                    )
//                }
//            }
//
//
//            if (viewState.isLoading) {
//                LoadingBox()
//            }
//        }
//
//        if (showAddSlideDialog) {
//            AlertDialog(
//                onDismissRequest = { showAddSlideDialog = false },
//                title = { Text("Add Slide") },
//                text = {
//                    androidx.compose.material3.OutlinedTextField(
//                        value = slideContent,
//                        onValueChange = { slideContent = it },
//                        label = { Text("Markdown Content (use # for titles)") },
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(top = 8.dp),
//                        maxLines = 10
//                    )
//                },
//                confirmButton = {
//                    Button(
//                        onClick = {
//                            if (slideContent.isNotBlank()) {
//                                onAddSlideClick(slideContent, navigator, webViewState)
//                                showAddSlideDialog = false
//                                slideContent = ""
//                            }
//                        },
//                        enabled = slideContent.isNotBlank()
//                    ) {
//                        Text("Add")
//                    }
//                },
//                dismissButton = {
//                    Button(onClick = {
//                        showAddSlideDialog = false
//                        slideContent = ""
//                    }) {
//                        Text("Cancel")
//                    }
//                }
//            )
//        }
//
//        if (showSaveDialog) {
//            AlertDialog(
//                onDismissRequest = { showSaveDialog = false },
//                containerColor = MaterialTheme.colorScheme.surface,
//                title = { Text("Save Presentation") },
//                text = { Text("Do you want to save your presentation before exiting?") },
//                confirmButton = {
//                    TextButton(
//                        onClick = {
//                            showSaveDialog = false
//                            onSavedClick()
//                        },
//                        enabled = viewState.slides.count() >= 1,
//                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
//                    ) {
//                        Text("Save")
//                    }
//                },
//                dismissButton = {
//                    TextButton(
//                        onClick = {
//                            showSaveDialog = false
//                            onDiscardPresentation()
//                            onBack()
//                        },
//                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
//                    ) {
//                        Text("Discard")
//                    }
//                }
//            )
//        }
//    }
//}