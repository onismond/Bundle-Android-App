package com.bundlegh.bundle.data.repositories

import com.bundlegh.bundle.data.db.AppDatabase
import com.bundlegh.bundle.data.db.entities.Category
import com.bundlegh.bundle.data.db.entities.School
import com.bundlegh.bundle.data.db.entities.SubCategory
import com.bundlegh.bundle.data.db.entities.User
import com.bundlegh.bundle.data.network.MyApi
import com.bundlegh.bundle.data.network.SafeApiRequest
import com.bundlegh.bundle.data.network.responses.AuthResponse
import com.bundlegh.bundle.data.network.responses.SchoolResponse
import retrofit2.Response

class UserRepository(
    private val api: MyApi,
    private val db: AppDatabase
): SafeApiRequest() {

    suspend fun userLogin(email: String, password: String) : AuthResponse {
        return apiRequest { api.userLogin(email, password) }
    }

    suspend fun userSignup(
        name: String,
        username: String,
        email: String,
        school: String,
        password: String
    ): AuthResponse {
        return apiRequest { api.userSignup(name, username, email, school, password) }
    }

    fun saveUser(user: User) = db.getUserDao().upsert(user)

    fun getUser() = db.getUserDao().getuser()

    fun saveCategory(category: Category) = db.getCategoryDao().upsert(category)

    fun saveSubCategory(subCategory: SubCategory) = db.getSubCategoryDao().upsert(subCategory)

    fun saveSchool(school: School) = db.getSchoolDao().upsert(school)

    fun getSchools() = db.getSchoolDao().getSchools()

    suspend fun loadSchools(): SchoolResponse {
        return apiRequest { api.loadSchools() }
    }
}