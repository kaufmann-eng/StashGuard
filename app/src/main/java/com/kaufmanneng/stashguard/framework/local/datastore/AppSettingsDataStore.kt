package com.kaufmanneng.stashguard.framework.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.kaufmanneng.stashguard.domain.model.ScreenTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("settings")

class AppSettingsDataStore(private val context: Context) {

    private companion object {
        val THEME_KEY = stringPreferencesKey("screen_theme")
    }

    val theme: Flow<ScreenTheme> = context.dataStore.data
        .map { preferences ->
            val themeName = preferences[THEME_KEY] ?: ScreenTheme.SYSTEM.name
            ScreenTheme.valueOf(themeName)
        }

    suspend fun setTheme(theme: ScreenTheme) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.name
        }
    }
}