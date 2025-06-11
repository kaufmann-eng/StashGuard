package com.kaufmanneng.stashguard.presentation.productcategorymanagement

import com.kaufmanneng.stashguard.domain.model.ProductCategory

data class ProductCategoryManagementState(
    val customCategories: List<ProductCategory> = emptyList(),
    val isLoading: Boolean = true
)
