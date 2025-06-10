package com.kaufmanneng.stashguard.framework.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kaufmanneng.stashguard.framework.local.converters.DateTimeConverters

@Database(
    entities = [ProductEntity::class, ProductCategoryEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(DateTimeConverters::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun productCategoryDao(): ProductCategoryDao

    companion object {

        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getInstance(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "stash_guard_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}