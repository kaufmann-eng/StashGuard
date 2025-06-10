package com.kaufmanneng.stashguard.framework.local.database

import com.kaufmanneng.stashguard.domain.model.ProductCategory

fun ProductCategoryEntity.toDomain(): ProductCategory {
    return ProductCategory(
        id = id,
        name = name,
        isDefault = isDefault
    )
}

fun ProductCategory.toEntity(): ProductCategoryEntity {
    return ProductCategoryEntity(
        id = id,
        name = name,
        isDefault = isDefault
    )
}