package com.kaufmanneng.stashguard.framework.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "product_categories")
data class ProductCategoryEntity(
    @PrimaryKey
    val id: UUID,
    val name: String,
    val isDefault: Boolean
)
