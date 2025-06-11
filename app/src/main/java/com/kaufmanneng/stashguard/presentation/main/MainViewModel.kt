package com.kaufmanneng.stashguard.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaufmanneng.stashguard.domain.model.ScreenTheme
import com.kaufmanneng.stashguard.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class MainViewModel(settingsRepository: SettingsRepository) : ViewModel() {
    val theme = settingsRepository.getTheme()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ScreenTheme.SYSTEM
        )
}