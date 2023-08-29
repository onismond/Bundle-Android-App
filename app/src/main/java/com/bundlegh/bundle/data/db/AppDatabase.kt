package com.bundlegh.bundle.data.db

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bundlegh.bundle.data.db.entities.*

@Database(
    entities = [User::class, Product::class, Cart::class, Order::class, UserProduct::class,
        Category::class, SubCategory::class, SliderImage::class, BottomAd::class, TopAd::class,
        School::class, Profile::class],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
    ],
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getUserDao() : UserDao
    abstract fun getCurrentProductDao() : CurrentProductDao
    abstract fun getCartDao() : CartDao
    abstract fun getOrderDao() : OrderDao
    abstract fun getUserProductDao() : UserProductDao
    abstract fun getCategoryDao() : CategoryDao
    abstract fun getSubCategoryDao() : SubCategoryDao
    abstract fun getSliderImageDao() : SliderImageDao
    abstract fun getBottomAdsDao() : BottomAdDao
    abstract fun getTopAdsDao() : TopAdDao
    abstract fun getSchoolDao() : SchoolDao
    abstract fun getProfileDao() : ProfileDao

    companion object{
        @Volatile
        private var instance: AppDatabase? = null
        private var LOCK = Any()

        operator fun invoke(context: Context) = instance?: synchronized(LOCK){
            instance?:buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "BundleDatabase.db"
            ).build()
    }
}