package com.kaufmanneng.stashguard.presentation.productcategorymanagement

import com.kaufmanneng.stashguard.domain.model.ProductCategory

sealed interface ProductCategoryManagementAction {
    data class OnAddCategoryClicked(val name: String) : ProductCategoryManagementAction
    data class OnUpdateCategoryClicked(val category: ProductCategory) : ProductCategoryManagementAction
    data class OnDeleteCategoryClicked(val category: ProductCategory) : ProductCategoryManagementAction
}