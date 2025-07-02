package app.ma.reveal

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import app.ma.reveal.common.DeviceConfiguration
import app.ma.reveal.common.ui.AppNavigation
import app.ma.reveal.common.ui.theme.RevealTheme

@Composable
fun RevealApp() {
    RevealTheme {
        AppNavigation()
    }
}