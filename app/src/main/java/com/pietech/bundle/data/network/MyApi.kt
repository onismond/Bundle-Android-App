package com.pietech.bundle.data.network

import com.pietech.bundle.data.network.responses.*
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface MyApi {

    @GET("index")
    suspend fun index() : Response<IndexResponse>

    @FormUrlEncoded
    @POST("search")
    suspend fun search(
        @Field("keyword") keyword: String
    ) : Response<SearchResponse>

    @FormUrlEncoded
    @POST("product/similar")
    suspend fun getSimilarProducts(
        @Field("product_id") product_id: String
    ) : Response<SimilarProsuctsResponse>

    @FormUrlEncoded
    @POST("order/new")
    suspend fun order(
        @Field("token") token: String,
        @Field("cart") cart: String
    ) : Response<OrderResponse>

    @FormUrlEncoded
    @POST("orders/load")
    suspend fun loadOrders(
        @Field("token") token: String,
    ) : Response<OrdersResponse>

    @FormUrlEncoded
    @POST("user/products")
    suspend fun getUserProducts(
        @Field("token") token: String,
    ) : Response<UserProductResponse>

    @FormUrlEncoded
    @POST("categories")
    suspend fun loadCategories(
        @Field("token") token: String,
    ) : Response<CategoryResponse>

    @FormUrlEncoded
    @POST("category")
    suspend fun getCategoryProduct(
        @Field("token") token: String,
        @Field("category_id") categoryId: String,
    ) : Response<ProductsResponse>

    @FormUrlEncoded
    @POST("subcategory")
    suspend fun getSubCategoryProducts(
        @Field("subcategory_id") subCategoryId: String,
    ) : Response<ProductsResponse>

    @FormUrlEncoded
    @POST("subcategories")
    suspend fun loadSubCategories(
        @Field("token") token: String,
    ) : Response<SubCategoryResponse>

    @GET("schools")
    suspend fun loadSchools()
    : Response<SchoolResponse>

    @Multipart
    @POST("product/new")
    suspend fun newProduct(
        @Part images: MutableList<MultipartBody.Part>,
        @Part("data") data: String,
    ) : Response<NewProductResponse>

    @FormUrlEncoded
    @POST("profile/update")
    suspend fun updateProfile(
        @Field("token") token: String,
        @Field("name") name: String,
        @Field("username") userName: String,
        @Field("contact") contact: String,
        @Field("school") school: Int,
    ) : Response<AuthResponse>

    @Multipart
    @POST("profile/change-avatar")
    suspend fun changeAvatar(
        @Part image: MultipartBody.Part,
        @Part("data") data: String,
    ) : Response<AuthResponse>

    @FormUrlEncoded
    @POST("profile/change-password")
    suspend fun changePassword(
        @Field("token") token: String,
        @Field("old") oldPassword: String,
        @Field("new") newPassword: String
    ) : Response<DefaultResponse>

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
                .baseUrl("http://192.168.45.83/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MyApi::class.java)
        }
    }
}