package com.kaufmanneng.stashguard.presentation.productlist

import java.util.UUID

sealed interface ProductListNavigationEvent {
    data class OnNavigateToProductForm(val productId: UUID?) : ProductListNavigationEvent
    data object OnNavigateToCategoryManagement : ProductListNavigationEvent
}