package org.company.app.platform

import android.content.Context
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SharedPreferencesSettings
import org.company.app.AndroidApp.Companion.APP_CONTEXT_INSTANCE

actual fun createLocalPref(name: String): ObservableSettings {
    val sharedPref =
        APP_CONTEXT_INSTANCE.getSharedPreferences(name, Context.MODE_PRIVATE)
    return SharedPreferencesSettings(sharedPref)
}
