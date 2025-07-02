package app.ma.reveal.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black

@Composable
fun SplitPane(
    modifier: Modifier = Modifier,
    startPane: @Composable () -> Unit,
    endPane: @Composable () -> Unit,
    startPaneWeight: Float = 0.4f,
    endPaneWeight: Float = 0.6f
) {
    Row(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(startPaneWeight)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            startPane()
        }
        VerticalDivider(color = Black.copy(alpha = .15f))
        Box(
            modifier = Modifier
                .weight(endPaneWeight)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.background)
        ) {
            endPane()
        }
    }
}