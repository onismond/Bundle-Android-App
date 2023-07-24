package com.bundlegh.bundle.ui.auth

import androidx.lifecycle.ViewModel
import com.bundlegh.bundle.data.db.entities.Category
import com.bundlegh.bundle.data.db.entities.School
import com.bundlegh.bundle.data.db.entities.SubCategory
import com.bundlegh.bundle.data.db.entities.User
import com.bundlegh.bundle.data.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthViewModel(
    private val repository: UserRepository
): ViewModel() {

    fun getLoggedInUser() = repository.getUser()

    suspend fun userLogin(
        email: String,
        password: String,
    ) = withContext(Dispatchers.IO){repository.userLogin(email, password)}

    suspend fun userSignup(
        name: String,
        username: String,
        email: String,
        school: String,
        password: String,
    ) = withContext(Dispatchers.IO){repository.userSignup(name, username, email, school, password)}

    suspend fun saveLoggedInUser(user: User) = repository.saveUser(user)

    fun saveCategory(category: Category) = repository.saveCategory(category)

    fun saveSubCategory(subCategory: SubCategory) = repository.saveSubCategory(subCategory)

    fun saveSchool(school: School) = repository.saveSchool(school)

    fun getSchools() = repository.getSchools()

    suspend fun loadSchools() = withContext(Dispatchers.IO){
        repository.loadSchools()
    }


}