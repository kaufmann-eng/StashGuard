package com.kaufmanneng.stashguard.domain.repository

import com.kaufmanneng.stashguard.domain.model.ProductCategory
import kotlinx.coroutines.flow.Flow

interface ProductCategoryRepository {
    fun getAllCategories(): Flow<List<ProductCategory>>
    suspend fun addCategory(name: String)
    suspend fun updateCategory(category: ProductCategory)
    suspend fun deleteCategory(category: ProductCategory)
}