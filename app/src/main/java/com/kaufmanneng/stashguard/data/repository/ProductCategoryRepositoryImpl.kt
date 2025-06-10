package com.kaufmanneng.stashguard.data.repository

import com.kaufmanneng.stashguard.data.datasource.ProductCategoryDataSource
import com.kaufmanneng.stashguard.domain.model.ProductCategory
import com.kaufmanneng.stashguard.domain.repository.ProductCategoryRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class ProductCategoryRepositoryImpl(
    private val localDataSource: ProductCategoryDataSource
) : ProductCategoryRepository {

    override fun getAllCategories(): Flow<List<ProductCategory>> {
        return localDataSource.getAllCategories()
    }

    override suspend fun addCategory(name: String) {
        val newCategory = ProductCategory(
            id = UUID.randomUUID(),
            name = name,
            isDefault = false
        )
        localDataSource.upsertCategory(newCategory)
    }

    override suspend fun updateCategory(category: ProductCategory) {
        localDataSource.upsertCategory(category)
    }

    override suspend fun deleteCategory(category: ProductCategory) {
        localDataSource.deleteCategory(category)
    }
}