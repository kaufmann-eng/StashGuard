package com.kaufmanneng.stashguard.presentation.productlist

import com.kaufmanneng.stashguard.domain.model.Product

data class ProductListState(
    val products: List<Product> = emptyList(),
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val isLoading: Boolean = true
)
