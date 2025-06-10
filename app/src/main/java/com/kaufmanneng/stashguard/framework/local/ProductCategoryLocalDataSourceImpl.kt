package com.kaufmanneng.stashguard.framework.local

import com.kaufmanneng.stashguard.data.datasource.ProductCategoryDataSource
import com.kaufmanneng.stashguard.domain.model.ProductCategory
import com.kaufmanneng.stashguard.framework.local.database.ProductCategoryDao
import com.kaufmanneng.stashguard.framework.local.database.toDomain
import com.kaufmanneng.stashguard.framework.local.database.toEntity
import com.kaufmanneng.stashguard.framework.provider.DefaultProductCategoryProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductCategoryLocalDataSourceImpl(
    private val dao: ProductCategoryDao
) : ProductCategoryDataSource {

    override fun getAllCategories(): Flow<List<ProductCategory>> {
        val defaultCategories = DefaultProductCategoryProvider.getDefaults()
        return dao.getCategories().map { entities ->
            val customCategory = entities.map { entity -> entity.toDomain() }
            customCategory + defaultCategories
        }
    }

    override suspend fun upsertCategory(category: ProductCategory) {
        if (category.isDefault) {
            dao.upsertCategory(category.toEntity())
        }
    }
    override suspend fun deleteCategory(category: ProductCategory) {
        if (category.isDefault) {
            dao.deleteCategory(category.toEntity())
        }
    }
}