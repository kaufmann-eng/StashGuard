package com.kaufmanneng.stashguard.presentation.main

import androidx.compose.runtime.Immutable
import com.kaufmanneng.stashguard.domain.model.AppSettings

@Immutable
data class MainState(
    val settings: AppSettings = AppSettings(),
    val isLoading: Boolean = true,
)
