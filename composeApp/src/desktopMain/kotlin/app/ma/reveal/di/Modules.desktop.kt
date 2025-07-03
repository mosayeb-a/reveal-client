package app.ma.reveal.di

import app.ma.reveal.common.createDesktopDataSource
import app.ma.reveal.data.KCEFDataStore
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module
    get() = module {
        single { KCEFDataStore(createDesktopDataSource()) }
    }