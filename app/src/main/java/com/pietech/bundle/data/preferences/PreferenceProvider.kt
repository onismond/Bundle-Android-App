package com.pietech.bundle.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

private const val KEY_SAVED_AT = "key_saved_at"
private const val KEY_TOKEN = "key_token"

class PreferenceProvider (
    context: Context
) {

    private val appContext = context.applicationContext

    private val preference: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(appContext)

    fun saveLastSavedAt(savedAt: String) {
        preference.edit().putString(
            KEY_SAVED_AT,
            savedAt
        ).apply()
    }

    fun saveToken(token: String) {
        preference.edit().putString(
            KEY_TOKEN,
            token
        ).apply()
    }

    fun getToken(): String? {
        return preference.getString(KEY_TOKEN, null)
    }

    fun getLastSavedAt(): String? {
        return preference.getString(KEY_SAVED_AT, null)
    }

}