package app.ma.reveal.feature.list.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.ma.reveal.common.formatDate
import app.ma.reveal.domain.Presentation
import app.ma.reveal.common.ui.theme.Blue
import app.ma.reveal.common.ui.theme.DarkGray

@Composable
fun PresentationItem(
    presentation: Presentation,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                if (isSelected) {
                    MaterialTheme.colorScheme.tertiary
                } else {
                    MaterialTheme.colorScheme.surface
                }
            )
            .clickable { onClick(presentation.path) }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = presentation.name,
                style = MaterialTheme.typography.titleMedium
                    .copy(
                        color = if (isSelected) MaterialTheme.colorScheme.onTertiary else
                            MaterialTheme.colorScheme.onSurface
                    ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(0.85f)
            )
            Spacer(Modifier.width(18.dp))
            Text(
                text = if (presentation.isAsset) "Example" else formatDate(presentation.addedDate),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (isSelected) MaterialTheme.colorScheme.onTertiary else
                        DarkGray
                ),
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = "${presentation.slideCount} slide${if (presentation.slideCount != 1) "s" else ""}",
            style = MaterialTheme.typography.bodySmall
                .copy(color = if (isSelected) MaterialTheme.colorScheme.onTertiary else Blue.copy(alpha = .8f)),
            modifier = Modifier.padding()
        )
    }
}