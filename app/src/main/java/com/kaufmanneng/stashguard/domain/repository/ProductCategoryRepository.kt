package com.kaufmanneng.stashguard.domain.repository

import com.kaufmanneng.stashguard.domain.model.ProductCategory
import kotlinx.coroutines.flow.Flow

interface ProductCategoryRepository {
    fun getCategories(): Flow<List<ProductCategory>>
    suspend fun addOrUpdateCategory(category: ProductCategory)
    suspend fun deleteCategory(category: ProductCategory)
}