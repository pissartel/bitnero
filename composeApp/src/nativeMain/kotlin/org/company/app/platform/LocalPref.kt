package org.company.app.platform

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.ObservableSettings

actual fun createLocalPref(name: String): ObservableSettings {
    return NSUserDefaultsSettings.Factory().create(name)
}