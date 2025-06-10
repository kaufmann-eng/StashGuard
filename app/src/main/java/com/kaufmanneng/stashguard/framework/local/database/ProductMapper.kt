package com.kaufmanneng.stashguard.framework.local.database

import com.kaufmanneng.stashguard.domain.model.Product
import com.kaufmanneng.stashguard.domain.model.ProductCategory

fun ProductEntity.toDomain(productCategory: ProductCategory): Product {
    return Product(
        id = id,
        name = name,
        productCategory = productCategory,
        expirationDate = expirationDate,
        quantity = quantity,
        addedDate = addedDate
    )
}

fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        id = id,
        name = name,
        productCategoryId = productCategory.id,
        expirationDate = expirationDate,
        quantity = quantity,
        addedDate = addedDate
    )
}