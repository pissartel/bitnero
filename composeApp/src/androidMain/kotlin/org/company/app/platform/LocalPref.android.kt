package org.company.app.platform

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.company.app.AndroidApp.Companion.APP_CONTEXT_INSTANCE

actual fun createLocalPref(name: String): ObservableSettings {
    val sharedPref =
        APP_CONTEXT_INSTANCE.getSharedPreferences(name, Context.MODE_PRIVATE)
    return SharedPreferencesSettings(sharedPref)
}

actual fun createEncryptedLocalPref(name: String): Settings {
    val masterKey = MasterKeys.AES256_GCM_SPEC.keystoreAlias


    val encryptedPreferences = EncryptedSharedPreferences.create(
        masterKey,
        APP_CONTEXT_INSTANCE.packageName + name,
        APP_CONTEXT_INSTANCE,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    return SharedPreferencesSettings(encryptedPreferences)
}