package app.ma.reveal.common

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.dp
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

expect fun configureWebView(webView: NativeWebView)