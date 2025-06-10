package com.kaufmanneng.stashguard.framework.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import java.util.UUID

@Dao
interface ProductDao {
    @Upsert
    suspend fun upsertProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' ORDER BY expirationDate ASC")
    fun getProducts(query: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: UUID): ProductEntity?

    @Query("SELECT * FROM products WHERE name = :name AND category = :category AND expirationDate = :expirationDate")
    suspend fun findByDetails(name: String, category: String, expirationDate: LocalDate): ProductEntity?

    @Query("SELECT * FROM products WHERE expirationDate <= :date")
    suspend fun getProductsExpiringBy(date: LocalDate): List<ProductEntity>
}