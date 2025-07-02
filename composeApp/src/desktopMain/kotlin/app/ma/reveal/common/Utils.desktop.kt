package app.ma.reveal.common

import com.multiplatform.webview.web.NativeWebView
import dev.datlag.kcef.KCEF
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.filesDir
import io.ktor.server.cio.CIO
import io.ktor.server.cio.CIOApplicationEngine
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.files
import io.ktor.server.http.content.static
import io.ktor.server.response.respondFile
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.io.File


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

    if (!dynamicFilesPath.exists()) {
        dynamicFilesPath.mkdirs()
    }

    return embeddedServer(CIO, port = port) {
        routing {
            static("/files/reveal") {
                files(staticResourcesPath)
                println(
                    "[${
                        Clock.System.now().toLocalDateTime(TimeZone.UTC).toString().substring(0, 19)
                            .replace('T', ' ')
                    }] 🛠️ Serving static resources from: $staticResourcesPath"
                )
            }
            static("/slides") {
                files(dynamicFilesPath)
                println(
                    "[${
                        Clock.System.now().toLocalDateTime(TimeZone.UTC).toString().substring(0, 19)
                            .replace('T', ' ')
                    }] 🛠️ Serving dynamic files from: $dynamicFilesPath"
                )
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
                    println(
                        "[${
                            Clock.System.now().toLocalDateTime(TimeZone.UTC).toString()
                                .substring(0, 19).replace('T', ' ')
                        }] 🛠️ Serving default index.html from: $staticResourcesPath"
                    )
                }
            }
        }
    }.start(wait = false)
}

actual fun getBaseAssetPath(): String = "http://127.0.0.1:8080/files/reveal"

actual fun configureWebView(webView: NativeWebView) {

}