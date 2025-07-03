package app.ma.reveal.common

import app.ma.reveal.data.KCEFDataStore
import com.multiplatform.webview.web.NativeWebView
import dev.datlag.kcef.KCEF
import io.ktor.server.cio.CIO
import io.ktor.server.cio.CIOApplicationEngine
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.files
import io.ktor.server.http.content.static
import io.ktor.server.routing.routing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


actual suspend fun kcefSetup(
    onInitialized: () -> Unit,
    onError: (Throwable?) -> Unit,
) {
    val kcefDataStore = koinInject<KCEFDataStore>()

    withContext(Dispatchers.IO) {
        try {
            if (!kcefDataStore.checkAndSetInitialized()) {
                KCEF.init(
                    builder = {
                        installDir(File("kcef-bundle"))
                        progress {
                            onInitialized {
                                onInitialized()
                            }
                        }
                        settings {
                            cachePath = File("cache").absolutePath
                            windowlessRenderingEnabled = true
                            noSandbox = true
                        }
                    },
                    onError = { error ->
                        error?.printStackTrace()
                        launch {
                            kcefDataStore.setInitialized(false)
                        }
                        onError(error)
                    },
                    onRestartRequired = {
                        println("KCEF restart required.")
                    }
                )
            } else {
                onInitialized()
            }
        } catch (e: Exception) {
            launch {
                kcefDataStore.setInitialized(false)
            }
            onError(e)
        }
    }
}

fun startStaticServer(port: Int = 8080): EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine.Configuration> {
    val staticResourcesPath = File("src/commonMain/composeResources/files/reveal").absoluteFile
    return embeddedServer(CIO, port = port) {
        routing {
            static("/files/reveal") {
                files(staticResourcesPath)
            }
        }
    }.start(wait = false)
}

actual fun getBaseAssetPath(): String = "http://127.0.0.1:8080/files/reveal"

actual fun configureWebView(webView: NativeWebView) {

}