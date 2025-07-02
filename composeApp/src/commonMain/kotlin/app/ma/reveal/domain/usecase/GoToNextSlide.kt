package app.ma.reveal.domain.usecase

import com.multiplatform.webview.web.WebViewNavigator

class GoToNextSlide {
    operator fun invoke(navigator: WebViewNavigator) {
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