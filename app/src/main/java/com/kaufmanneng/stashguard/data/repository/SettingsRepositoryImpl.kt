package com.kaufmanneng.stashguard.data.repository

import com.kaufmanneng.stashguard.data.datasource.SettingsLocalDataSource
import com.kaufmanneng.stashguard.domain.model.AppSettings
import com.kaufmanneng.stashguard.domain.model.ScreenTheme
import com.kaufmanneng.stashguard.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl(
    private val localDataSource: SettingsLocalDataSource
) : SettingsRepository {

    override fun getSettings(): Flow<AppSettings> {
        return localDataSource.getSettings()
    }

    override suspend fun setTheme(theme: ScreenTheme) {
        localDataSource.setTheme(theme)
    }

    override suspend fun setUseDynamicColor(useDynamic: Boolean) {
        localDataSource.setUseDynamicColor(useDynamic)
    }

}