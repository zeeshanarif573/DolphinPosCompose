package com.retail.dolphinpos.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.retail.dolphinpos.data.dao.CustomerDao
import com.retail.dolphinpos.data.dao.ProductsDao
import com.retail.dolphinpos.data.dao.UserDao
import com.retail.dolphinpos.data.entities.category.CategoryEntity
import com.retail.dolphinpos.data.entities.customer.CustomerEntity
import com.retail.dolphinpos.data.entities.products.CachedImageEntity
import com.retail.dolphinpos.data.entities.products.ProductImagesEntity
import com.retail.dolphinpos.data.entities.products.ProductsEntity
import com.retail.dolphinpos.data.entities.products.VariantImagesEntity
import com.retail.dolphinpos.data.entities.products.VariantsEntity
import com.retail.dolphinpos.data.entities.products.VendorEntity
import com.retail.dolphinpos.data.entities.user.ActiveUserDetailsEntity
import com.retail.dolphinpos.data.entities.user.BatchEntity
import com.retail.dolphinpos.data.entities.user.LocationEntity
import com.retail.dolphinpos.data.entities.user.RegisterEntity
import com.retail.dolphinpos.data.entities.user.RegisterStatusEntity
import com.retail.dolphinpos.data.entities.user.StoreEntity
import com.retail.dolphinpos.data.entities.user.StoreLogoUrlEntity
import com.retail.dolphinpos.data.entities.user.UserEntity

@Database(
    entities = [UserEntity::class, StoreEntity::class, StoreLogoUrlEntity::class, LocationEntity::class, RegisterEntity::class,
        ActiveUserDetailsEntity::class, BatchEntity::class, RegisterStatusEntity::class, CategoryEntity::class, ProductsEntity::class,
        ProductImagesEntity::class, VariantsEntity::class, VariantImagesEntity::class, VendorEntity::class, CustomerEntity::class,
        CachedImageEntity::class],
    version = 3,
    exportSchema = false
)
abstract class DolphinDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun productsDao(): ProductsDao
    abstract fun customerDao(): CustomerDao

    companion object {
        @Volatile
        private var INSTANCE: DolphinDatabase? = null

        fun getDatabase(context: Context): DolphinDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, DolphinDatabase::class.java, "dolphin_retail_pos"
                ).addCallback(object : Callback() {
                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        db.execSQL("PRAGMA foreign_keys = ON;")
                    }
                })
                    //.addMigrations(MIGRATION_47_48)
                    //.fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

//        val MIGRATION_47_48 = object : Migration(47, 48) {
//            override fun migrate(db: SupportSQLiteDatabase) {
//                db.execSQL("ALTER TABLE products ADD COLUMN sku TEXT")
//            }
//        }

        // Method to delete the database file
//        fun deleteDatabase(context: Context) {
//            context.deleteDatabase("lingerie_pos_local_database")
//            INSTANCE = null // Reset the INSTANCE so Room can recreate it when needed
//        }

    }
}