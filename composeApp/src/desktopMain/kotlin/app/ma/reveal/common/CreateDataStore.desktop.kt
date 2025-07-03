package app.ma.reveal.common

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import java.io.File

fun createDesktopDataSource(): DataStore<Preferences> {
    return createDataStore {
        val file = File(System.getProperty("user.home"), ".reveal/$DATA_STORE_FILE_NAME")
        file.parentFile.mkdirs()
        file.absolutePath
    }
}