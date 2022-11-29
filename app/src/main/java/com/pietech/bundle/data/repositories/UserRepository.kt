package com.pietech.bundle.data.repositories

import com.pietech.bundle.data.db.AppDatabase
import com.pietech.bundle.data.db.entities.User
import com.pietech.bundle.data.network.MyApi
import com.pietech.bundle.data.network.SafeApiRequest
import com.pietech.bundle.data.network.responses.AuthResponse
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
        password: String
    ): AuthResponse {
        return apiRequest { api.userSignup(name, username, email, password) }
    }

    fun saveUser(user: User) = db.getUserDao().upsert(user)

    fun getUser() = db.getUserDao().getuser()
}