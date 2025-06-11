package com.kaufmanneng.stashguard.framework.local

import com.kaufmanneng.stashguard.data.datasource.SettingsLocalDataSource
import com.kaufmanneng.stashguard.domain.model.ScreenTheme
import com.kaufmanneng.stashguard.framework.local.datastore.AppSettingsDataStore
import kotlinx.coroutines.flow.Flow

class SettingsLocalDataSourceImpl(
    private val dataStore: AppSettingsDataStore
) : SettingsLocalDataSource {

    override fun getTheme(): Flow<ScreenTheme> {
        return dataStore.theme
    }

    override suspend fun setTheme(theme: ScreenTheme) {
        dataStore.setTheme(theme)
    }
}