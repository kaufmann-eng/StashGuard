package com.kaufmanneng.stashguard.presentation.productform

import com.kaufmanneng.stashguard.domain.model.ProductCategory
import kotlinx.datetime.LocalDate

data class ProductFormState(
    val name: String = "",
    val productCategory: ProductCategory? = null,
    val quantity: Int = 1,
    val expirationDate: LocalDate? = null,
    val availableCategories: List<ProductCategory> = emptyList(),
    val isSaving: Boolean = false,
    val isEditMode: Boolean = false
)
