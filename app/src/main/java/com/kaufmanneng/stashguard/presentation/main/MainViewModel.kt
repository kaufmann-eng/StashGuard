package com.kaufmanneng.stashguard.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaufmanneng.stashguard.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MainViewModel(settingsRepository: SettingsRepository) : ViewModel() {
    val state = settingsRepository.getSettings()
        .map { settings ->
            MainState(settings = settings, isLoading = false)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MainState(isLoading = true)
        )
}