package com.kaufmanneng.stashguard.presentation.settings

import androidx.compose.runtime.Immutable
import com.kaufmanneng.stashguard.domain.model.AppSettings

@Immutable
data class SettingsState(
    val settings: AppSettings = AppSettings(),
    val isLoading: Boolean = true,
)