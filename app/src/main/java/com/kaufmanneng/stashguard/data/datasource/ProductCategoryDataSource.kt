package com.kaufmanneng.stashguard.data.datasource

import com.kaufmanneng.stashguard.domain.model.ProductCategory
import kotlinx.coroutines.flow.Flow

interface ProductCategoryDataSource {
    fun getAllCategories(): Flow<List<ProductCategory>>

    suspend fun upsertCategory(category: ProductCategory)

    suspend fun deleteCategory(category: ProductCategory)
}