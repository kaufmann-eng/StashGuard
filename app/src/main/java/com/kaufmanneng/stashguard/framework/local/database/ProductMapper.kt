package com.kaufmanneng.stashguard.framework.local.database

import com.kaufmanneng.stashguard.domain.model.Product

fun ProductEntity.toDomain(): Product {
    return Product(
        id = id,
        name = name,
        category = category,
        expirationDate = expirationDate,
        quantity = quantity,
        addedDate = addedDate
    )
}

fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        id = id,
        name = name,
        category = category,
        expirationDate = expirationDate,
        quantity = quantity,
        addedDate = addedDate
    )
}