package com.kaufmanneng.stashguard.framework.local

import android.R.attr.category
import android.util.Log
import com.kaufmanneng.stashguard.data.datasource.ProductLocalDataSource
import com.kaufmanneng.stashguard.domain.model.Product
import com.kaufmanneng.stashguard.domain.model.ProductCategory
import com.kaufmanneng.stashguard.framework.local.database.ProductCategoryDao
import com.kaufmanneng.stashguard.framework.local.database.ProductCategoryEntity
import com.kaufmanneng.stashguard.framework.local.database.ProductDao
import com.kaufmanneng.stashguard.framework.local.database.ProductEntity
import com.kaufmanneng.stashguard.framework.local.database.toDomain
import com.kaufmanneng.stashguard.framework.local.database.toEntity
import com.kaufmanneng.stashguard.framework.provider.DefaultProductCategoryProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import java.util.UUID

class ProductLocalDataSourceImpl(
    private val productDao: ProductDao,
    private val productCategoryDao: ProductCategoryDao
) : ProductLocalDataSource {

    override fun getProducts(query: String): Flow<List<Product>> {
        return productDao.getProducts(query).map { entityList ->
            entityList.map { entity ->
                entity.toDomain(getProductCategory(entity.productCategoryId))
            }
        }
    }

    override suspend fun getProductById(id: UUID): Product? {
        return productDao.getProductById(id)?.let {
            it.toDomain(getProductCategory(it.productCategoryId))
        }
    }

    override suspend fun findProductByDetails(
        name: String,
        productCategoryId: UUID,
        expirationDate: LocalDate
    ): Product? {
        return productDao.findByDetails(
            name = name,
            productCategoryId = productCategoryId,
            expirationDate = expirationDate
        )?.let {
            it.toDomain(getProductCategory(it.productCategoryId))
        }
    }

    override suspend fun getProductsExpiringBy(date: LocalDate): List<Product> {
        return productDao.getProductsExpiringBy(date).map { entity ->
            entity.toDomain(getProductCategory(entity.productCategoryId))
        }
    }

    override suspend fun upsertProduct(product: Product) {
        productDao.upsertProduct(product.toEntity())
    }

    override suspend fun deleteProduct(product: Product) {
        productDao.deleteProduct(product.toEntity())
    }

    private suspend fun getProductCategory(id: UUID): ProductCategory {
        val productCategories = productCategoryDao.getCategories().first().map { it.toDomain() } + DefaultProductCategoryProvider.getDefaults()
        val productCategory = productCategories.find { it.id == id }
        return productCategory ?: run {
            Log.e("ProductLocalDataSourceImpl", "Error getting product category")
            DefaultProductCategoryProvider.getDefault()
        }
    }
}