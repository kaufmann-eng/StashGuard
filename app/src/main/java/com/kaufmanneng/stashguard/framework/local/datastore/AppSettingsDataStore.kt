package com.kaufmanneng.stashguard.framework.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.kaufmanneng.stashguard.domain.model.AppSettings
import com.kaufmanneng.stashguard.domain.model.ScreenTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("settings")

class AppSettingsDataStore(private val context: Context) {

    private companion object {
        val THEME_KEY = stringPreferencesKey("screen_theme")
        val DYNAMIC_COLOR_KEY = booleanPreferencesKey("dynamic_color")
    }

    val appSettings: Flow<AppSettings> = context.dataStore.data
        .map { preferences ->
            val theme = ScreenTheme.valueOf(preferences[THEME_KEY] ?: ScreenTheme.SYSTEM.name)
            val useDynamicColor = preferences[DYNAMIC_COLOR_KEY] ?: true
            AppSettings(theme, useDynamicColor)
        }

    suspend fun setTheme(theme: ScreenTheme) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.name
        }
    }

    suspend fun setUseDynamicColor(useDynamic: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DYNAMIC_COLOR_KEY] = useDynamic
        }
    }
}