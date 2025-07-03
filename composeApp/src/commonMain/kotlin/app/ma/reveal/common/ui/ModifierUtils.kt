package app.ma.reveal.common.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max


fun Modifier.scaleOnPress(
    pressedScale: Float = 0.72f,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null
): Modifier = composed {
    if (!enabled) return@composed this

    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressedScale else 1f,
        label = "scaleOnPressAnim"
    )

    this
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    isPressed = true
                    try {
                        val success = tryAwaitRelease()
                        if (success && onClick != null) {
                            onClick()
                        }
                    } finally {
                        isPressed = false
                    }
                }
            )
        }
        .scale(scale)
}


fun Modifier.verticalScrollbar(
    scrollState: ScrollState,
    scrollBarWidth: Dp = 4.dp,
    minScrollBarHeight: Dp = 5.dp,
    scrollBarColor: Color = Color.Blue,
    cornerRadius: Dp = 2.dp
): Modifier = composed {
    val targetAlpha = if (scrollState.isScrollInProgress) 1f else 0f
    val duration = if (scrollState.isScrollInProgress) 150 else 500

    val alpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = duration)
    )

    drawWithContent {
        drawContent()

        val needDrawScrollbar = scrollState.isScrollInProgress || alpha > 0.0f

        if (needDrawScrollbar && scrollState.maxValue > 0) {
            val visibleHeight: Float = this.size.height - scrollState.maxValue
            val scrollBarHeight: Float = max(
                visibleHeight * (visibleHeight / this.size.height),
                minScrollBarHeight.toPx()
            )
            val scrollPercent: Float = scrollState.value.toFloat() / scrollState.maxValue
            val scrollBarOffsetY: Float = scrollState.value +
                    (visibleHeight - scrollBarHeight) * scrollPercent

            drawRoundRect(
                color = scrollBarColor,
                topLeft = Offset(this.size.width - scrollBarWidth.toPx(), scrollBarOffsetY),
                size = Size(scrollBarWidth.toPx(), scrollBarHeight),
                alpha = alpha,
                cornerRadius = CornerRadius(cornerRadius.toPx())
            )
        }
    }
}

fun Modifier.cardBottomElevation(): Modifier = this.then(
    Modifier.drawWithContent {
        val paddingPx = 8.dp.toPx()
        clipRect(
            left = 0f,
            top = 0f,
            right = size.width,
            bottom = size.height + paddingPx
        ) {
            this@drawWithContent.drawContent()
        }
    }
)
