package com.kaufmanneng.stashguard.framework.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface ProductCategoryDao {

    @Query("SELECT * FROM product_categories ORDER BY name ASC")
    fun getCategories(): Flow<List<ProductCategoryEntity>>

    @Query("SELECT * FROM product_categories WHERE id = :id")
    suspend fun getCategoryById(id: UUID): ProductCategoryEntity?

    @Upsert
    suspend fun upsertCategory(category: ProductCategoryEntity)

    @Delete
    suspend fun deleteCategory(category: ProductCategoryEntity)

    /*@Transaction
    fun seedDefaultCategories() {
        val defaultCategories = listOf(
            ProductCategoryEntity(id = UUID.randomUUID(), name = "product_category_canned_goods", isDefault = false),
            ProductCategoryEntity(id = UUID.randomUUID(), name = "product_category_pasta_rice", isDefault = false)
        )
        upsertCategoriesBlocking(defaultCategories)
    }

    @Upsert
    fun upsertCategoriesBlocking(categories: List<ProductCategoryEntity>)*/
}