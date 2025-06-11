package com.kaufmanneng.stashguard.presentation.productlist

import com.kaufmanneng.stashguard.domain.model.Product

sealed interface ProductListAction {
    data class OnSearchQueryChanged(val query: String) : ProductListAction
    data class OnSearchActiveChanged(val isActive: Boolean) : ProductListAction
    data object OnAddProductClicked : ProductListAction
    data class OnDeleteProduct(val product: Product) : ProductListAction
    data class OnUndoDeleteClicked(val product: Product) : ProductListAction
    data class OnProductClicked(val product: Product) : ProductListAction
    data object OnManageCategoriesClicked : ProductListAction
    data object OnSettingsClicked : ProductListAction
}