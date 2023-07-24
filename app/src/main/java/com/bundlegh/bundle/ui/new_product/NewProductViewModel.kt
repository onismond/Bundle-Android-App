package com.bundlegh.bundle.ui.new_product

import androidx.lifecycle.ViewModel
import com.google.gson.reflect.TypeToken
import com.bundlegh.bundle.data.db.entities.Category
import com.bundlegh.bundle.data.db.entities.SubCategory
import com.bundlegh.bundle.data.db.entities.User
import com.bundlegh.bundle.data.repositories.NewProductRepository
import com.bundlegh.bundle.data.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody.Part
import okhttp3.RequestBody

class NewProductViewModel(
    private val repository: NewProductRepository
): ViewModel() {

    fun getUserToken() = repository.getUserToken()

    fun saveCategory(category: Category) = repository.saveCategory(category)

    fun getCategories() = repository.getCategories()

    suspend fun loadCategories() = withContext(Dispatchers.IO){
        repository.loadCategories(getUserToken())
    }

    fun saveSubCategory(subCategory: SubCategory) = repository.saveSubCategory(subCategory)

    fun getSubCategories(category_name: String) = repository.getSubCategories(category_name)

    suspend fun loadSubCategories() = withContext(Dispatchers.IO){
        repository.loadSubCategories(getUserToken())
    }

    suspend fun newProduct(images: MutableList<Part>, data: String) = withContext(Dispatchers.IO){
        repository.newProduct(images, data)
    }


//    fun getLoggedInUser() = repository.getUser()
//
//    suspend fun userLogin(
//        email: String,
//        password: String,
//    ) = withContext(Dispatchers.IO){repository.userLogin(email, password)}
//
//    suspend fun userSignup(
//        name: String,
//        username: String,
//        email: String,
//        password: String,
//    ) = withContext(Dispatchers.IO){repository.userSignup(name, username, email, password)}
//
//    suspend fun saveLoggedInUser(user: User) = repository.saveUser(user)
}