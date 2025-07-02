package app.ma.reveal.common.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import app.ma.reveal.common.DeviceConfiguration
import app.ma.reveal.feature.create.CreateSlides
import app.ma.reveal.feature.create.CreateSlidesViewModel
import app.ma.reveal.feature.list.PresentationList
import app.ma.reveal.feature.list.PresentationListViewModel
import app.ma.reveal.feature.present.Present
import app.ma.reveal.feature.present.PresentViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.reflect.KType

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)
    val isExpandedScreen = deviceConfiguration in listOf(
        DeviceConfiguration.TABLET_PORTRAIT,
        DeviceConfiguration.TABLET_LANDSCAPE,
        DeviceConfiguration.DESKTOP
    )

    NavHost(
        navController = navController,
        startDestination = "top_level_route",
        modifier = modifier
    ) {
        composable("top_level_route") {
            TopLevelRoute(
                isExpandedScreen = isExpandedScreen,
                deviceConfiguration = deviceConfiguration,
                onCreateClicked = { navController.navigate(CreateSlidesScreen) }
            )
        }

        baseComposable<CreateSlidesScreen> {
            val viewModel: CreateSlidesViewModel = koinViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            val webViewState by viewModel.webViewState.collectAsStateWithLifecycle()
            val navigator by viewModel.navigator.collectAsStateWithLifecycle()

            CreateSlides(
                viewState = state,
                webViewState = webViewState,
                navigator = navigator,
                onBack = { navController.popBackStack() },
                onSavedClick = {
                    viewModel.savePresentation { id ->
                        navController.navigate("top_level_route") {
                            popUpTo("top_level_route") { inclusive = true }
                        }
                    }
                },
                onPreviousSlideClick = { viewModel.previousSlide(it) },
                onNextSlideClick = { viewModel.nextSlide(it) },
                onAddSlideClick = { content, nav, state ->
                    viewModel.addSlide(content, nav, state!!)
                },
                onDiscardPresentation = { viewModel.discardPresentation() }
            )
        }
    }
}

@Composable
private fun TopLevelRoute(
    isExpandedScreen: Boolean,
    deviceConfiguration: DeviceConfiguration,
    onCreateClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isExpandedScreen) {
        val nestedNavController = rememberNavController()
        val viewModel: PresentationListViewModel = koinViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()

        val currentBackStackEntry by nestedNavController.currentBackStackEntryAsState()
        val selectedPath = remember(currentBackStackEntry) {
            val route = currentBackStackEntry?.destination?.route
            if (route?.equals("app.ma.reveal.common.ui.PresentScreen/{path}") == true) {
                val path = currentBackStackEntry?.toRoute<PresentScreen>()?.path
                path
            } else {
                null
            }
        }

        SplitPane(
            startPane = {
                PresentationList(
                    onPresentationSelected = { path ->
                        nestedNavController.navigate(PresentScreen(path))
                    },
                    onCreateClicked = onCreateClicked,
                    viewState = state,
                    modifier = Modifier.fillMaxSize(),
                    selectedPath = selectedPath
                )
            },
            endPane = {
                NavHost(
                    navController = nestedNavController,
                    startDestination = "empty",
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable("empty") {
                        Message(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.secondary)
                                .fillMaxSize(),
                            message = "No presentation selected.\nPlease choose one from the list on the left.",
                            faces = EmptyStateFaces.suggestion
                        )
                    }

                    baseComposable<PresentScreen> {
                        val args = it.toRoute<PresentScreen>()
                        val presentViewModel: PresentViewModel =
                            koinViewModel { parametersOf(args.path) }
                        val webViewState by presentViewModel.webViewState.collectAsStateWithLifecycle()
                        val navigator by presentViewModel.navigator.collectAsStateWithLifecycle()

                        Present(
                            webViewSate = webViewState!!,
                            navigator = navigator,
                            onPreviousSlideClicked = { presentViewModel.previousSlide(navigator) },
                            onNextSlideClicked = { presentViewModel.nextSlide(navigator) },
                            modifier = Modifier.fillMaxSize(),
                            deviceConfiguration = deviceConfiguration
                        )
                    }
                }
            },
            modifier = modifier.fillMaxSize()
        )
    } else {
        val nestedNavController = rememberNavController()
        NavHost(
            navController = nestedNavController,
            startDestination = PresentationListScreen,
            modifier = modifier.fillMaxSize()
        ) {
            baseComposable<PresentationListScreen> {
                val viewModel: PresentationListViewModel = koinViewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()

                PresentationList(
                    onPresentationSelected = { path ->
                        nestedNavController.navigate(PresentScreen(path))
                    },
                    onCreateClicked = onCreateClicked,
                    viewState = state,
                    modifier = Modifier.fillMaxSize()
                )
            }

            baseComposable<PresentScreen> {
                val args = it.toRoute<PresentScreen>()
                val viewModel: PresentViewModel = koinViewModel { parametersOf(args.path) }
                val webViewState by viewModel.webViewState.collectAsStateWithLifecycle()
                val navigator by viewModel.navigator.collectAsStateWithLifecycle()

                Present(
                    webViewSate = webViewState!!,
                    navigator = navigator,
                    onPreviousSlideClicked = { viewModel.previousSlide(navigator) },
                    onNextSlideClicked = { viewModel.nextSlide(navigator) },
                    modifier = Modifier.fillMaxSize(),
                    deviceConfiguration = deviceConfiguration
                )
            }
        }
    }
}

inline fun <reified T : Any> NavGraphBuilder.baseComposable(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
    noinline content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    this.composable<T>(
        typeMap = typeMap,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
    ) {
        content(it)
    }
}