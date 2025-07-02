package app.ma.reveal.common

import android.webkit.WebSettings
import com.multiplatform.webview.web.NativeWebView


actual suspend fun kcefSetup(
    onInitialized: () -> Unit,
    onError: (Throwable?) -> Unit
) {
    onInitialized()
}

actual fun getBaseAssetPath(): String =
    "file:///android_asset/composeResources/reveal.composeapp.generated.resources/files/reveal"


actual fun configureWebView(webView: NativeWebView) {
    webView.apply {
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        isVerticalScrollBarEnabled = false
        isHorizontalScrollBarEnabled = false
    }
}
