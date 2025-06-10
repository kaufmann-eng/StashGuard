package com.kaufmanneng.stashguard.presentation.productform

import kotlinx.datetime.LocalDate

sealed interface ProductFormAction {
    data class OnNameChanged(val name: String) : ProductFormAction
    data class OnCategoryChanged(val category: String) : ProductFormAction
    data class OnDateSelected(val date: LocalDate) : ProductFormAction
    data object OnIncrementQuantity : ProductFormAction
    data object OnDecrementQuantity : ProductFormAction
    data object OnSaveClicked : ProductFormAction
    data object OnBackNavigationClicked : ProductFormAction
}