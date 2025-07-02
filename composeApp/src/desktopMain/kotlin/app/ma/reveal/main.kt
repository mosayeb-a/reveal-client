package app.ma.reveal

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import app.ma.reveal.common.startStaticServer
import app.ma.reveal.di.initKoin
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.github.vinceglb.filekit.FileKit

fun main() {
    initKoin()
    FileKit.init(appId = "DesktopApplication")
    Napier.base(DebugAntilog())
    val server = startStaticServer(8080)
    application {
        Window(
            onCloseRequest = {
                server.stop(1000, 2000)
                exitApplication()
            },
            title = "reveal",
            alwaysOnTop = true
        ) {
            RevealApp()
        }
    }
}
