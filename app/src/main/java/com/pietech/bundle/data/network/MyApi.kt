package com.pietech.bundle.data.network

import com.pietech.bundle.data.db.entities.Cart
import com.pietech.bundle.data.db.entities.User
import com.pietech.bundle.data.network.responses.AuthResponse
import com.pietech.bundle.data.network.responses.IndexResponse
import com.pietech.bundle.data.network.responses.OrderResponse
import com.pietech.bundle.data.network.responses.SearchResponse
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface MyApi {

    @GET("index")
    suspend fun index() : Response<IndexResponse>

    @FormUrlEncoded
    @POST("search")
    suspend fun search(
        @Field("keyword") keyword: String
    ) : Response<SearchResponse>

    @FormUrlEncoded
    @POST("order")
    suspend fun order(
        @Field("token") token: String,
        @Field("cart") cart: String
    ) : Response<OrderResponse>

    @FormUrlEncoded
    @POST("login")
    suspend fun userLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ) : Response<AuthResponse>

    @FormUrlEncoded
    @POST("register")
    suspend fun userSignup(
        @Field("name") name: String,
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ) : Response<AuthResponse>

    companion object{
        operator fun invoke(
            networkConnectionInterceptor: NetworkConnectionInterceptor
        ) : MyApi{
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(networkConnectionInterceptor)
                .build()
            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("http://192.168.246.83/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MyApi::class.java)
        }
    }
}