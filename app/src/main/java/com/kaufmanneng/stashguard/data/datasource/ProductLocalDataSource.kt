package com.kaufmanneng.stashguard.data.datasource

import com.kaufmanneng.stashguard.domain.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import java.util.UUID

interface ProductLocalDataSource {

    fun getProducts(query: String): Flow<List<Product>>

    suspend fun getProductById(id: UUID): Product?

    suspend fun findProductByDetails(
        name: String,
        productCategoryId: UUID,
        expirationDate:
        LocalDate
    ): Product?

    suspend fun getProductsExpiringBy(date: LocalDate): List<Product>

    suspend fun upsertProduct(product: Product)

    suspend fun deleteProduct(product: Product)
}