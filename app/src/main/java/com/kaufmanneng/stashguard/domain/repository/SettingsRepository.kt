package com.kaufmanneng.stashguard.domain.repository

import com.kaufmanneng.stashguard.domain.model.ScreenTheme
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getTheme(): Flow<ScreenTheme>
    suspend fun setTheme(theme: ScreenTheme)
}