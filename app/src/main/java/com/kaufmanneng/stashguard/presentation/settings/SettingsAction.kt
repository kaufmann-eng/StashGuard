package com.kaufmanneng.stashguard.presentation.settings

import com.kaufmanneng.stashguard.domain.model.ScreenTheme

sealed interface SettingsAction {
    data class OnThemeSelected(val theme: ScreenTheme): SettingsAction
    data class OnDynamicColorChanged(val isEnabled: Boolean): SettingsAction
}