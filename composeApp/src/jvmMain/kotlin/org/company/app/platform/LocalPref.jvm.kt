package org.company.app.platform

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.PreferencesSettings
import java.util.prefs.Preferences

actual fun createLocalPref(name: String): ObservableSettings {
    val preferences = Preferences.userRoot()
    return PreferencesSettings(preferences.node(name))
}