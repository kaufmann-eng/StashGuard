package com.kaufmanneng.stashguard.presentation.productlist

import com.kaufmanneng.stashguard.domain.model.Product
import com.kaufmanneng.stashguard.domain.model.ProductCategory

data class ProductListState(
    val groupedProducts: Map<ProductCategory, List<Product>> = emptyMap(),
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val isLoading: Boolean = true
)
