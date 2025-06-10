package com.kaufmanneng.stashguard.presentation.productform

sealed interface ProductFormNavigationEvent {
    data object OnNavigateBack : ProductFormNavigationEvent
}