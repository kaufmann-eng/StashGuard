package com.kaufmanneng.stashguard.framework.local

import com.kaufmanneng.stashguard.data.datasource.ProductLocalDataSource
import com.kaufmanneng.stashguard.domain.model.Product
import com.kaufmanneng.stashguard.framework.local.database.ProductDao
import com.kaufmanneng.stashguard.framework.local.database.toDomain
import com.kaufmanneng.stashguard.framework.local.database.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import java.util.UUID

class ProductLocalDataSourceImpl(
    private val productDao: ProductDao
) : ProductLocalDataSource {

    override fun getProducts(query: String): Flow<List<Product>> {
        return productDao.getProducts(query).map { entityList ->
            entityList.map { entity -> entity.toDomain() }
        }
    }

    override suspend fun getProductById(id: UUID): Product? {
        return productDao.getProductById(id)?.toDomain()
    }

    override suspend fun findProductByDetails(
        name: String,
        category: String,
        expirationDate: LocalDate
    ): Product? {
        return productDao.findByDetails(name, category, expirationDate)?.toDomain()
    }

    override suspend fun getProductsExpiringBy(date: LocalDate): List<Product> {
        return productDao.getProductsExpiringBy(date).map { entity -> entity.toDomain() }
    }

    override suspend fun upsertProduct(product: Product) {
        productDao.upsertProduct(product.toEntity())
    }

    override suspend fun deleteProduct(product: Product) {
        productDao.deleteProduct(product.toEntity())
    }
}