package com.kaufmanneng.stashguard.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class AppSettings(
    val theme: ScreenTheme = ScreenTheme.SYSTEM,
    val useDynamicColor: Boolean = true,
)
