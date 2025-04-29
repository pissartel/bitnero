package org.company.app.platform

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import java.util.prefs.Preferences

actual fun createLocalPref(name: String): ObservableSettings {
    val preferences = Preferences.userRoot()
    return PreferencesSettings(preferences.node(name))
}

actual fun createEncryptedLocalPref(name: String): Settings {
    TODO("Not yet implemented")
}