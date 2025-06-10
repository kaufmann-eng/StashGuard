package com.kaufmanneng.stashguard.framework.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate
import java.util.UUID

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val id: UUID,
    val name: String,
    val category: String,
    val expirationDate: LocalDate,
    val quantity: Int,
    val addedDate: LocalDate,
)
