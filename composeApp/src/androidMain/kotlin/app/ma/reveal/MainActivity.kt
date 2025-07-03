package app.ma.reveal

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import app.ma.reveal.common.ui.theme.Gray
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.manualFileKitCoreInitialization

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(navigationBarStyle = SystemBarStyle.light(Gray.toArgb(), Gray.toArgb()))
        super.onCreate(savedInstanceState)
        FileKit.manualFileKitCoreInitialization(this)
        Napier.base(DebugAntilog())
        setContent {
            RevealApp()
        }
    }

}