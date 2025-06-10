package com.kaufmanneng.stashguard.presentation.productform

import kotlinx.datetime.LocalDate

data class ProductFormState(
    val name: String = "",
    val category: String = "",
    val expirationDate: LocalDate? = null,
    val quantity: Int = 1,
    val isSaving: Boolean = false,
    val isEditMode: Boolean = false
)
