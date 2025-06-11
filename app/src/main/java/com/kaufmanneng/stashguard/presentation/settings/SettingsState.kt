package com.kaufmanneng.stashguard.presentation.settings

import com.kaufmanneng.stashguard.domain.model.ScreenTheme

data class SettingsState(
    val currentTheme: ScreenTheme = ScreenTheme.SYSTEM,
)
