package com.kaufmanneng.stashguard.domain.repository

import com.kaufmanneng.stashguard.domain.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import java.util.UUID

interface ProductRepository {

    fun getProducts(query: String): Flow<List<Product>>

    suspend fun getProductById(id: UUID): Product?

    suspend fun findProductByDetails(name: String, productCategoryId: UUID, expirationDate: LocalDate): Product?

    suspend fun getProductsExpiringSoon(daysInAdvance: Int): List<Product>

    suspend fun addOrUpdateProduct(product: Product)

    suspend fun deleteProduct(product: Product)
}