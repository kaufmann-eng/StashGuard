package com.kaufmanneng.stashguard.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route

@Serializable
data object ProductList : Route

@Serializable
data class ProductForm(val productId: String?) : Route

@Serializable
data object ProductCategoryManagement : Route