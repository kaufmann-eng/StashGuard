package com.kaufmanneng.stashguard.domain.model

import java.util.UUID

data class ProductCategory(
    val id: UUID,
    val name: String,
    val isDefault: Boolean
)
