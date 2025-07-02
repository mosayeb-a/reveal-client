package app.ma.reveal.common.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
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
    NavHost(
        navController = navController,
        startDestination = PresentationListScreen(),
        modifier = modifier
    ) {
        baseComposable<PresentationListScreen> {
            val args = it.toRoute<PresentationListScreen>()
            val viewModel: PresentationListViewModel =
                koinViewModel { parametersOf(args.presentationId, args.fromAssets, args.fromFiles) }
            val state by viewModel.state.collectAsStateWithLifecycle()

            PresentationList(
                onPresentationSelected = { path ->
                    navController.navigate(PresentScreen(path))
                },
                onCreateClicked = {
                    navController.navigate(CreateSlidesScreen)
                },
                viewState = state,

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
                        navController.navigate(
                            route = PresentationListScreen(presentationId = id)
                        ) {
                            popUpTo<PresentationListScreen> { inclusive = false }
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

        baseComposable<PresentScreen> {
            val args = it.toRoute<PresentScreen>()
            val viewModel: PresentViewModel = koinViewModel { parametersOf(args.path) }
            val webViewSate by viewModel.webViewState.collectAsStateWithLifecycle()
            val navigator by viewModel.navigator.collectAsStateWithLifecycle()
            Present(
                webViewSate = webViewSate!!,
                navigator = navigator,
                onPreviousSlideClicked = { viewModel.previousSlide(navigator) },
                onNextSlideClicked = { viewModel.nextSlide(navigator) },
            )
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