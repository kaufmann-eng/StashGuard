package com.kaufmanneng.stashguard.data.repository

import android.R.attr.category
import com.kaufmanneng.stashguard.data.datasource.ProductLocalDataSource
import com.kaufmanneng.stashguard.domain.model.Product
import com.kaufmanneng.stashguard.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import java.util.UUID

class ProductRepositoryImpl(
    private val localDataSource: ProductLocalDataSource
) : ProductRepository {

    override fun getProducts(query: String): Flow<List<Product>> {
        return localDataSource.getProducts(query)
    }

    override suspend fun getProductById(id: UUID): Product? {
        return localDataSource.getProductById(id)
    }

    override suspend fun findProductByDetails(
        name: String,
        productCategoryId: UUID,
        expirationDate: LocalDate
    ): Product? {
        return localDataSource.findProductByDetails(
            name = name,
            productCategoryId = productCategoryId,
            expirationDate = expirationDate
        )
    }

    override suspend fun getProductsExpiringSoon(daysInAdvance: Int): List<Product> {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val thresholdDate = today.plus(daysInAdvance, DateTimeUnit.DAY)
        return localDataSource.getProductsExpiringBy(thresholdDate)
    }

    override suspend fun addOrUpdateProduct(product: Product) {
        localDataSource.upsertProduct(product)
    }

    override suspend fun deleteProduct(product: Product) {
        localDataSource.deleteProduct(product)
    }


}