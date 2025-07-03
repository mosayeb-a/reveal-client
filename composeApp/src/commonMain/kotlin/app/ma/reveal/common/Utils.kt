package app.ma.reveal.common

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.multiplatform.webview.web.IWebView
import com.multiplatform.webview.web.NativeWebView
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import okio.Path.Companion.toPath
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.java.KoinJavaComponent.inject
import java.text.SimpleDateFormat
import java.util.Locale
import org.koin.core.component.get
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