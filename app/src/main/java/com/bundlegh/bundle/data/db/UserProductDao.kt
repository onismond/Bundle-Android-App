package com.bundlegh.bundle.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bundlegh.bundle.data.db.entities.CURRENT_PRODUCT_ID
import com.bundlegh.bundle.data.db.entities.Product
import com.bundlegh.bundle.data.db.entities.UserProduct

@Dao
interface UserProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(userProduct: UserProduct): Long

    @Query("SELECT * FROM userproduct WHERE id=:productId")
    fun getUserProduct(productId: String) : UserProduct

    @Query("SELECT * FROM userproduct")
    fun getproducts() : LiveData<MutableList<UserProduct>>

    @Delete
    fun removeUserProduct(userProduct: UserProduct) : Int

    @Query("DELETE FROM `userproduct`")
    fun removeUserProduct()
}
