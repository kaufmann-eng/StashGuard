package com.kaufmanneng.stashguard.framework.provider

import android.content.Context
import androidx.annotation.StringRes
import com.kaufmanneng.stashguard.R
import com.kaufmanneng.stashguard.domain.model.ProductCategory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.UUID

private data class DefaultProductCategoryDefinition(
    val key: String,
    //@StringRes
    val nameResId: Int
)

object DefaultProductCategoryProvider : KoinComponent {

    private val context: Context by inject()

    private val defaultDefinition = DefaultProductCategoryDefinition(
        key = "product_category_undefined",
        nameResId = R.string.product_category_undefined
    )

    private val allDefinitions = listOf<DefaultProductCategoryDefinition>(
        defaultDefinition,
        DefaultProductCategoryDefinition(
            key = "product_category_canned_goods",
            nameResId = R.string.product_category_canned_goods
        ),
        DefaultProductCategoryDefinition(
            key = "product_category_pasta_rice",
            nameResId = R.string.product_category_pasta_rice
        ),
    )

    fun getDefault(): ProductCategory {
        return ProductCategory(
            id = UUID.nameUUIDFromBytes(defaultDefinition.key.toByteArray()),
            name = context.getString(defaultDefinition.nameResId),
            isDefault = true
        )
    }

    fun getDefaults(): List<ProductCategory> {
        return allDefinitions.map { definition ->
            ProductCategory(
                id = UUID.nameUUIDFromBytes(definition.key.toByteArray()),
                name = context.getString(definition.nameResId),
                isDefault = true
            )
        }
    }
}