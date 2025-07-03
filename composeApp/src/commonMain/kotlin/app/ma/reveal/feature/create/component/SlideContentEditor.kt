package app.ma.reveal.feature.create.component


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import app.ma.reveal.common.ui.scaleOnPress
import app.ma.reveal.common.ui.verticalScrollbar
import kotlinx.coroutines.launch

@Composable
fun SlideContentEditor(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onActionClick: () -> Unit = {}
) {
    val maxLines = 5
    val lineHeight = 24.dp
    val baseHeight = 56.dp
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    var previousLength by remember { mutableIntStateOf(value.length) }

    val visibleHeight = with(density) {
        (baseHeight + (maxLines - 1) * lineHeight).toPx()
    }

    val iconAlpha by animateFloatAsState(
        targetValue = if (value.isBlank()) 0.7f else 1f,
        label = "icon alpha anim"
    )

    LaunchedEffect(value) {
        val isAppending = value.length > previousLength && value.endsWith("\n")
        val isAtBottom = scrollState.value + visibleHeight >= scrollState.maxValue - 20f // allow ~20px tolerance

        if (isAppending && isAtBottom) {
            scope.launch {
                scrollState.animateScrollTo(scrollState.maxValue)
            }
        }

        previousLength = value.length
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = baseHeight, max = baseHeight + (maxLines - 1) * lineHeight)
            .background(MaterialTheme.colorScheme.surface),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .heightIn(min = baseHeight, max = baseHeight + (maxLines - 1) * lineHeight)
        ) {
            TextField(
                value = value,
                onValueChange = { newValue ->
                    onValueChange(newValue)
                },
                placeholder = {
                    Text(
                        text = "Markdown content",
                        color = Black.copy(alpha = .43f)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .verticalScrollbar(
                        scrollState = scrollState,
                        scrollBarColor = Black.copy(alpha = .27f)
                    )
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            scope.launch {
                                val maxScroll = scrollState.maxValue.toFloat()
                                val targetScroll = (maxScroll / 2)
                                    .coerceAtMost(maxScroll)
                                scrollState.animateScrollTo(targetScroll.toInt())
                            }
                        }
                    },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                ),
                maxLines = Int.MAX_VALUE
            )
        }

        Spacer(Modifier.width(10.dp))

        IconButton(
            modifier = Modifier
                .padding(bottom = 4.dp)
                .align(Alignment.Bottom)

            ,
            onClick = {  },
            enabled = value.isNotBlank()
        ) {
            Icon(
                modifier = Modifier.size(46.dp)
                    .scaleOnPress(pressedScale = .77f, enabled = value.isNotBlank()) {
                        onActionClick() // <- this is the actual click
                    }
//                    .scaleOnPress(pressedScale = .77f, enabled = value.isNotBlank())
                ,
                imageVector = Icons.Rounded.AddCircle,
                contentDescription = "Add Slide",
                tint = MaterialTheme.colorScheme.primary.copy(alpha = iconAlpha)
            )
        }
    }
}
