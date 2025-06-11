package com.kaufmanneng.stashguard.presentation.main

import com.kaufmanneng.stashguard.domain.model.ScreenTheme

data class MainState(
    val theme: ScreenTheme = ScreenTheme.SYSTEM,
    val isLoading: Boolean = true,
)
