package app.ma.reveal.di

import app.ma.reveal.data.repo.PresentationRepositoryImpl
import app.ma.reveal.data.repo.source.AssetDataSource
import app.ma.reveal.data.repo.source.FileDataSource
import app.ma.reveal.domain.repo.PresentationRepository
import app.ma.reveal.domain.usecase.GetPresentationById
import app.ma.reveal.domain.usecase.GetPresentations
import app.ma.reveal.domain.usecase.GoToNextSlide
import app.ma.reveal.domain.usecase.GoToPreviousSlide
import app.ma.reveal.domain.usecase.GoToSlide
import app.ma.reveal.domain.usecase.SavePresentation
import app.ma.reveal.feature.create.CreateSlidesViewModel
import app.ma.reveal.feature.list.PresentationListViewModel
import app.ma.reveal.feature.present.PresentViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    singleOf(::AssetDataSource)
    singleOf(::FileDataSource)
    singleOf(::PresentationRepositoryImpl).bind<PresentationRepository>()

    single { GoToNextSlide() }
    single { GoToPreviousSlide() }
    single { GetPresentations(get()) }
    single { SavePresentation(get()) }
    single { GoToSlide() }
    single { GetPresentationById(get()) }

    viewModel { (presentationId: String?, fromAssets: Boolean, fromFiles: Boolean) ->
        PresentationListViewModel(
            get(),
            get(),
            presentationId,
            fromAssets,
            fromFiles
        )
    }
    viewModel { CreateSlidesViewModel(get(), get(), get(), get()) }
    viewModel { (path: String) -> PresentViewModel(get(), get(), get(), path) }
}

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(appModule)
    }
}