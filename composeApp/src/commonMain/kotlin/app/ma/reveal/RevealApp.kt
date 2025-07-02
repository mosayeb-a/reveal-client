package app.ma.reveal

import androidx.compose.runtime.Composable
import app.ma.reveal.common.ui.AppNavigation
import app.ma.reveal.common.ui.theme.RevealTheme

@Composable
fun RevealApp() {
    RevealTheme {
        AppNavigation()
    }
}