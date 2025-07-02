package app.ma.reveal.common

import io.ktor.server.cio.CIO
import io.ktor.server.cio.CIOApplicationEngine
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.default
import io.ktor.server.http.content.files
import io.ktor.server.http.content.resources
import io.ktor.server.http.content.static
import io.ktor.server.routing.routing
import java.io.File
import dev.datlag.kcef.KCEF
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import reveal.composeapp.generated.resources.Res
import androidx.compose.ui.window.application
import androidx.compose.ui.window.Window
import com.multiplatform.webview.web.NativeWebView
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.filesDir
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.routing.*
import io.ktor.server.http.content.*
import io.ktor.server.response.respondFile
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


actual suspend fun kcefSetup(
    onInitialized: () -> Unit,
    onError: (Throwable?) -> Unit
) {
    withContext(Dispatchers.IO) {
        KCEF.init(builder = {
            installDir(File("kcef-bundle"))
            progress {
                onInitialized {
                    onInitialized()
                }
            }
            settings {
                cachePath = File("cache").absolutePath
            }
        }, onError = {
            it?.printStackTrace()
            onError(it)
        }, onRestartRequired = {
            println("KCEF restart required.")
        })
    }
}

fun startStaticServer(port: Int = 8080): EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine.Configuration> {
    val staticResourcesPath = File("src/commonMain/composeResources/files/reveal").absoluteFile
    val dynamicFilesPath = File(FileKit.filesDir.absolutePath(), "reveal").absoluteFile
    println("[${Clock.System.now().toLocalDateTime(TimeZone.UTC).toString().substring(0, 19).replace('T', ' ')}] Starting static server on port $port")
    println("[${Clock.System.now().toLocalDateTime(TimeZone.UTC).toString().substring(0, 19).replace('T', ' ')}] Static resources path: $staticResourcesPath")
    println("[${Clock.System.now().toLocalDateTime(TimeZone.UTC).toString().substring(0, 19).replace('T', ' ')}] Dynamic files path: $dynamicFilesPath")

    if (!staticResourcesPath.exists()) {
        println("[${Clock.System.now().toLocalDateTime(TimeZone.UTC).toString().substring(0, 19).replace('T', ' ')}] ⚠️ Static resources path does not exist!")
    } else {
        println("[${Clock.System.now().toLocalDateTime(TimeZone.UTC).toString().substring(0, 19).replace('T', ' ')}] ✅ Static resources path exists and contains: ${staticResourcesPath.list()?.joinToString()}")
    }
    if (!dynamicFilesPath.exists()) {
        println("[${Clock.System.now().toLocalDateTime(TimeZone.UTC).toString().substring(0, 19).replace('T', ' ')}] ⚠️ Dynamic files path does not exist, creating: $dynamicFilesPath")
        dynamicFilesPath.mkdirs()
    } else {
        println("[${Clock.System.now().toLocalDateTime(TimeZone.UTC).toString().substring(0, 19).replace('T', ' ')}] ✅ Dynamic files path exists and contains: ${dynamicFilesPath.list()?.joinToString()}")
    }

    return embeddedServer(CIO, port = port) {
        routing {
            static("/files/reveal") {
                files(staticResourcesPath)
                println("[${Clock.System.now().toLocalDateTime(TimeZone.UTC).toString().substring(0, 19).replace('T', ' ')}] 🛠️ Serving static resources from: $staticResourcesPath")
            }
            static("/slides") {
                files(dynamicFilesPath)
                println("[${Clock.System.now().toLocalDateTime(TimeZone.UTC).toString().substring(0, 19).replace('T', ' ')}] 🛠️ Serving dynamic files from: $dynamicFilesPath")
            }
            static("/files/reveal") {
                files(staticResourcesPath)
            }
            static("/slides") {
                files(dynamicFilesPath)
            }
            get("/presentation.html") {
                call.respondFile(File(dynamicFilesPath, "slides/presentation.html"))
            }
            route("/") {
                get {
                    call.respondFile(File(staticResourcesPath, "index.html"))
                    println("[${Clock.System.now().toLocalDateTime(TimeZone.UTC).toString().substring(0, 19).replace('T', ' ')}] 🛠️ Serving default index.html from: $staticResourcesPath")
                }
            }
        }
    }.start(wait = false)
}

actual fun getBaseAssetPath(): String = "http://127.0.0.1:8080/files/reveal"

actual fun configureWebView(webView: NativeWebView) {

}