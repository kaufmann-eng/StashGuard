package com.kaufmanneng.stashguard.framework.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductCategoryDao {

    @Query("SELECT * FROM product_categories ORDER BY name ASC")
    fun getCategories(): Flow<List<ProductCategoryEntity>>

    @Upsert
    suspend fun upsertCategory(category: ProductCategoryEntity)

    @Delete
    suspend fun deleteCategory(category: ProductCategoryEntity)
}