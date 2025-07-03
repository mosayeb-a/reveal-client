package app.ma.reveal

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import app.ma.reveal.common.DATA_STORE_FILE_NAME
import app.ma.reveal.common.createDataStore
import app.ma.reveal.common.koinInject
import app.ma.reveal.common.startStaticServer
import app.ma.reveal.data.KCEFDataStore
import app.ma.reveal.di.initKoin
import dev.datlag.kcef.KCEF
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.github.vinceglb.filekit.FileKit
import kotlinx.coroutines.runBlocking

fun main() {
    initKoin()
    FileKit.init(appId = "DesktopApplication")
    Napier.base(DebugAntilog())
    val server = startStaticServer(8080)
    val kcefDataStore = koinInject<KCEFDataStore>()
    runBlocking {
        kcefDataStore.setInitialized(false)
    }
    application {
        Window(
            onCloseRequest = {
                println("onCloseRequest")
                runBlocking {
                    kcefDataStore.setInitialized(false)
                    server.stop(1000, 2000)
                }
                exitApplication()
            },
            title = "reveal",
            alwaysOnTop = true
        ) {
            RevealApp()
        }
    }
}