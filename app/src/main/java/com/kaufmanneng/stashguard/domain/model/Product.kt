package com.kaufmanneng.stashguard.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import java.util.UUID

data class Product(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val category: String,
    val expirationDate: LocalDate,
    val quantity: Int = 1,
    val addedDate: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
)
