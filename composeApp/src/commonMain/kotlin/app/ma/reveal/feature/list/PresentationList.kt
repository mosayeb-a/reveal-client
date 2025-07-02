package app.ma.reveal.feature.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.ma.reveal.common.ui.Appbar
import app.ma.reveal.common.ui.EmptyStateFaces
import app.ma.reveal.common.ui.LoadingBox
import app.ma.reveal.common.ui.Message
import app.ma.reveal.feature.list.component.PresentationItem
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun PresentationList(
    onPresentationSelected: (String) -> Unit,
    onCreateClicked: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PresentationListViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()
    var isExtended by remember { mutableStateOf(true) }

    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.isScrollInProgress }
            .distinctUntilChanged()
            .collect { isScrolling -> isExtended = !isScrolling }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Appbar {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Rounded.Menu,
                        contentDescription = "menu"
                    )
                }
                Text(text = "IDEAS")
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "search"
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.secondary,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreateClicked,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                containerColor = MaterialTheme.colorScheme.primary,
                icon = { Icon(Icons.Rounded.Add, contentDescription = "create presentation") },
                text = { Text("CREATE IDEA") },
                expanded = isExtended,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 3.dp
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (state.slides.isEmpty()) {
                Message(
                    message = "No presentations yet. Create one using the + button.",
                    faces = EmptyStateFaces.suggestion
                )
            } else {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.slides, key = { it.path }) { presentation ->
                        PresentationItem(
                            presentation = presentation,
                            onClick = onPresentationSelected,
                            modifier = Modifier.fillMaxWidth()
                        )
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    item("end_spacer") {
                        Spacer(Modifier.height(86.dp))
                    }
                }
            }

            if (state.isLoading) {
                LoadingBox()
            }
        }
    }
}