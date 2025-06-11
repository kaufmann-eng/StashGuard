package com.kaufmanneng.stashguard.data.datasource

import com.kaufmanneng.stashguard.domain.model.ScreenTheme
import kotlinx.coroutines.flow.Flow

interface SettingsLocalDataSource {
    fun getTheme(): Flow<ScreenTheme>
    suspend fun setTheme(theme: ScreenTheme)
}