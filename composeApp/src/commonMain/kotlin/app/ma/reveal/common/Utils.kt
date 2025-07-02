package app.ma.reveal.common

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.multiplatform.webview.web.IWebView
import com.multiplatform.webview.web.NativeWebView
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat
import java.util.Locale

expect suspend fun kcefSetup(
    onInitialized: () -> Unit,
    onError: (Throwable?) -> Unit = {}
)


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

fun Modifier.cardBottomElevation(): Modifier = this.then(
    Modifier.drawWithContent {
        val paddingPx = 8.dp.toPx()
        clipRect(
            left = 0f,
            top = 0f,
            right = size.width,
            bottom = size.height + paddingPx
        ) {
            this@drawWithContent.drawContent()
        }
    }
)

fun Modifier.cardBottomAndLeftElevation(): Modifier = this.then(
    Modifier.drawWithContent {
        val paddingPx = 8.dp.toPx()
        // Clip the content to include padding for bottom and left shadows
        clipRect(
            left = -paddingPx, // Extend left for shadow
            top = 0f,
            right = size.width,
            bottom = size.height + paddingPx
        ) {
            // Draw shadow on the left
            translate(left = -paddingPx) {
                drawRect(
                    color = Black.copy(alpha = 0.2f), // Shadow color and opacity
                    size = size.copy(width = paddingPx, height = size.height + paddingPx),
                    style = androidx.compose.ui.graphics.drawscope.Fill
                )
            }
            // Draw shadow on the bottom
//            translate(top = size.height) {
//                drawRect(
//                    color = Black.copy(alpha = 0.2f),
//                    size = size.copy(height = paddingPx),
//                    style = androidx.compose.ui.graphics.drawscope.Fill
//                )
//            }
            // Draw the content
            this@drawWithContent.drawContent()
        }
    }
)

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