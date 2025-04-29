package org.company.app.platform

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings


expect fun createLocalPref(name: String): ObservableSettings
expect fun createEncryptedLocalPref(name: String): Settings
