package com.kaufmanneng.stashguard.presentation.productlist

import com.kaufmanneng.stashguard.domain.model.Product

interface ProductListScreenEvent {
    data class ShowUndoDeleteSnackbar(val product: Product) : ProductListScreenEvent
}