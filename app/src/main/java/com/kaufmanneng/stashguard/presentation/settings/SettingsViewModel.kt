package com.kaufmanneng.stashguard.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaufmanneng.stashguard.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val state = settingsRepository.getSettings()
        .map { settings -> SettingsState(settings = settings, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsState(isLoading = true)
        )

    fun onAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.OnThemeSelected -> {
                viewModelScope.launch {
                    settingsRepository.setTheme(action.theme)
                }
            }
            is SettingsAction.OnDynamicColorChanged -> {
                viewModelScope.launch {
                    settingsRepository.setUseDynamicColor(action.isEnabled)
                }
            }
        }
    }
}