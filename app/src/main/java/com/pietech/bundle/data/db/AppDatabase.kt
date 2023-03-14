package com.pietech.bundle.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pietech.bundle.data.db.entities.Cart
import com.pietech.bundle.data.db.entities.Order
import com.pietech.bundle.data.db.entities.Product
import com.pietech.bundle.data.db.entities.User

@Database(
    entities = [User::class, Product::class, Cart::class, Order::class,],
    version = 3
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getUserDao() : UserDao
    abstract fun getCurrentProductDao() : CurrentProductDao
    abstract fun getCartDao() : CartDao
    abstract fun getOrderDao() : OrderDao

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