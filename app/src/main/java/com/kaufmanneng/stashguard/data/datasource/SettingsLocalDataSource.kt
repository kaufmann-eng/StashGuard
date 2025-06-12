package com.kaufmanneng.stashguard.data.datasource

import com.kaufmanneng.stashguard.domain.model.AppSettings
import com.kaufmanneng.stashguard.domain.model.ScreenTheme
import kotlinx.coroutines.flow.Flow

interface SettingsLocalDataSource {
    fun getSettings(): Flow<AppSettings>
    suspend fun setTheme(theme: ScreenTheme)
    suspend fun setUseDynamicColor(useDynamic: Boolean)
}