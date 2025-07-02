package app.ma.reveal.domain.usecase

import com.multiplatform.webview.web.WebViewNavigator

class GoToPreviousSlide {
    operator fun invoke(navigator: WebViewNavigator) {
        navigator.evaluateJavaScript(
            """
            if (window.revealDeck) {
                window.revealDeck.sync();
                window.revealDeck.prev();
            } else if (Reveal) {
                Reveal.prev();
            }
            """.trimIndent()
        )
    }
}