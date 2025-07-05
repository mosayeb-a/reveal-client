package app.ma.reveal.common

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import java.io.File

fun createDesktopDataSource(): DataStore<Preferences> {
    return createDataStore {
        DATA_STORE_FILE_NAME
    }
}