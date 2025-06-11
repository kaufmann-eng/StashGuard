package com.kaufmanneng.stashguard.data.repository

import com.kaufmanneng.stashguard.data.datasource.SettingsLocalDataSource
import com.kaufmanneng.stashguard.domain.model.ScreenTheme
import com.kaufmanneng.stashguard.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl(
    private val localDataSource: SettingsLocalDataSource
) : SettingsRepository {

    override fun getTheme(): Flow<ScreenTheme> {
        return localDataSource.getTheme()
    }

    override suspend fun setTheme(theme: ScreenTheme) {
        localDataSource.setTheme(theme)
    }

}