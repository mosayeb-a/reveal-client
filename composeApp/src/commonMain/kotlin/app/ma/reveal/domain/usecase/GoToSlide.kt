package app.ma.reveal.domain.usecase

import com.multiplatform.webview.web.WebViewNavigator
import kotlinx.coroutines.delay

class GoToSlide {
    suspend operator fun invoke(navigator: WebViewNavigator, slidesCount: Int) {
        delay(300L)
        repeat(slidesCount - 1) { index ->
            delay(50L)
            navigator.evaluateJavaScript(
                """
            if (window.revealDeck) {
                window.revealDeck.sync();
                window.revealDeck.next();
            } else if (Reveal) {
                Reveal.next();
            }
            """.trimIndent()
            )
        }
    }
}