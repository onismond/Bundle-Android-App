package com.bundlegh.bundle.data.repositories

import com.bundlegh.bundle.data.db.AppDatabase
import com.bundlegh.bundle.data.db.entities.Category
import com.bundlegh.bundle.data.db.entities.SubCategory
import com.bundlegh.bundle.data.db.entities.User
import com.bundlegh.bundle.data.network.MyApi
import com.bundlegh.bundle.data.network.SafeApiRequest
import com.bundlegh.bundle.data.network.responses.AuthResponse
import com.bundlegh.bundle.data.network.responses.CategoryResponse
import com.bundlegh.bundle.data.network.responses.NewProductResponse
import com.bundlegh.bundle.data.network.responses.SubCategoryResponse
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part
import okhttp3.RequestBody
import retrofit2.Response

class NewProductRepository(
    private val api: MyApi,
    private val db: AppDatabase
): SafeApiRequest() {

    fun getUserToken() = db.getUserDao().getUserToken()

    fun saveCategory(category: Category) = db.getCategoryDao().upsert(category)

    fun getCategories() = db.getCategoryDao().getCategories()

    suspend fun loadCategories(token: String): CategoryResponse {
        return apiRequest { api.loadCategories(token) }
    }

    fun saveSubCategory(subCategory: SubCategory) = db.getSubCategoryDao().upsert(subCategory)

    fun getSubCategories(category_name: String) = db.getSubCategoryDao().getSubCategories(category_name)

    suspend fun loadSubCategories(token: String): SubCategoryResponse {
        return apiRequest { api.loadSubCategories(token) }
    }

    suspend fun newProduct(images: MutableList<Part>, data: String): NewProductResponse {
        return apiRequest { api.newProduct(images, data) }
    }



}






