package app.ma.reveal.common.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
            PresentationList(
                onPresentationSelected = { path ->
                    navController.navigate(PresentScreen(path))
                },
                onCreateClicked = {
                    navController.navigate(CreateSlidesScreen)
                },
                viewModel = viewModel
            )
        }
        baseComposable<CreateSlidesScreen> {
            val viewModel: CreateSlidesViewModel = koinViewModel()
            CreateSlides(
                onNavigateToPresentationList = { id ->
                    navController.navigate(
                        route = PresentationListScreen(presentationId = id)
                    ) {
                        popUpTo<PresentationListScreen> { inclusive = false }
                    }
                },
                onBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }
        baseComposable<PresentScreen> {
            val args = it.toRoute<PresentScreen>()
            val viewModel: PresentViewModel =
                koinViewModel { parametersOf(args.path) }
            Present(
                viewModel = viewModel
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