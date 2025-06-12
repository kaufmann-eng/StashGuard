package com.kaufmanneng.stashguard.domain.repository

import com.kaufmanneng.stashguard.domain.model.AppSettings
import com.kaufmanneng.stashguard.domain.model.ScreenTheme
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): Flow<AppSettings>
    suspend fun setTheme(theme: ScreenTheme)
    suspend fun setUseDynamicColor(useDynamic: Boolean)
}