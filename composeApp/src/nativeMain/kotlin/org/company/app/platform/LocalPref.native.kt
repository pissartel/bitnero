package org.company.app.platform

import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.KeychainSettings
import com.russhwolf.settings.Settings
import platform.Foundation.NSBundle

@OptIn(ExperimentalSettingsImplementation::class)
actual fun createEncryptedLocalPref(name: String): Settings =
    KeychainSettings("${NSBundle.mainBundle.bundleIdentifier}.AUTH")
