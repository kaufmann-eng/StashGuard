package com.kaufmanneng.stashguard.framework.local.converters

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDate

class DateTimeConverters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate): String {
        return date.toString()
    }

    @TypeConverter
    fun toLocalDate(value: String): LocalDate {
        return LocalDate.parse(value)
    }
}