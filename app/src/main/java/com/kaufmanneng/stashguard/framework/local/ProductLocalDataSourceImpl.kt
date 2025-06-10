package com.kaufmanneng.stashguard.framework.local

import android.content.Context
import android.util.Log
import com.kaufmanneng.stashguard.R
import com.kaufmanneng.stashguard.data.datasource.ProductCategoryDataSource
import com.kaufmanneng.stashguard.data.datasource.ProductLocalDataSource
import com.kaufmanneng.stashguard.domain.model.Product
import com.kaufmanneng.stashguard.domain.model.ProductCategory
import com.kaufmanneng.stashguard.framework.local.database.ProductDao
import com.kaufmanneng.stashguard.framework.local.database.toDomain
import com.kaufmanneng.stashguard.framework.local.database.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate
import java.util.UUID

class ProductLocalDataSourceImpl(
    private val productDao: ProductDao,
    private val productCategoryDataSource: ProductCategoryDataSource,
    private val applicationContext: Context
) : ProductLocalDataSource {

    override fun getProducts(query: String): Flow<List<Product>> {
        val productsFlow = productDao.getProducts(query)
        val categoriesFlow = productCategoryDataSource.getAllCategories()

        return combine(productsFlow, categoriesFlow) { productEntities, allCategories ->
            val categoryMap = allCategories.associateBy { it.id }

            productEntities.map { productEntity ->
                val category = categoryMap[productEntity.productCategoryId]
                    ?: run {
                        Log.e("ProductLocalDataSourceImpl", "product category with id ${productEntity.productCategoryId} not found")
                        ProductCategory(
                            id = productEntity.productCategoryId,
                            name = applicationContext.getString(R.string.product_category_unknown),
                            isDefault = false
                        )
                    }
                productEntity.toDomain(category)
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
        val productCategories = productCategoryDataSource.getAllCategories().first()
        val productCategory = productCategories.find { it.id == id }
        return productCategory ?: run {
            Log.e("ProductLocalDataSourceImpl", "product category with id $id not found")
            ProductCategory(
                id = id,
                name = applicationContext.getString(R.string.product_category_unknown),
                isDefault = false
            )
        }
    }
}