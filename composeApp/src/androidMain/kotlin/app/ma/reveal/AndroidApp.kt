package app.ma.reveal

import android.app.Application
import androidx.compose.ui.graphics.Color
import app.ma.reveal.di.initKoin
import org.koin.android.ext.koin.androidContext


class AndroidApp: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@AndroidApp)
        }
    }
}