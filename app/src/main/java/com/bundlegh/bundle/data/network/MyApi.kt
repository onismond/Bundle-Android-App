package com.bundlegh.bundle.data.network

import com.bundlegh.bundle.data.network.responses.*
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
    @POST("profile")
    suspend fun loadProfile(
        @Field("token") token: String,
    ) : Response<ProfileResponse>

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
    @POST("product/update")
    suspend fun updateUserProduct(
        @Field("token") token: String,
        @Field("id") id: String,
        @Field("name") name: String,
        @Field("amount") amount: String,
        @Field("description") description: String,
    ) : Response<DefaultResponse>

    @FormUrlEncoded
    @POST("product/delete")
    suspend fun deleteUserProduct(
        @Field("token") token: String,
        @Field("id") id: String,
    ) : Response<DefaultResponse>

    @FormUrlEncoded
    @POST("product/images")
    suspend fun getProductImages(
        @Field("product_id") product_id: String,
    ) : Response<ImagesResponse>

    @FormUrlEncoded
    @POST("store/products")
    suspend fun getStoreProducts(
        @Field("store_id") store_id: String,
    ) : Response<ProductsResponse>

    @FormUrlEncoded
    @POST("order/new")
    suspend fun order(
        @Field("token") token: String,
        @Field("cart") cart: String,
        @Field("delivery_location") deliveryLocation: String,
        @Field("contact") contact: String,
        @Field("start_time") startTime: String,
        @Field("end_time") endTime: String,
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
    @POST("product/detail")
    suspend fun getProduct(
        @Field("product_id") product_id: String,
    ) : Response<ProductResponse>

    @FormUrlEncoded
    @POST("store/detail")
    suspend fun getStore(
        @Field("store_id") store_id: String,
    ) : Response<StoreResponse>

    @GET("account/types")
    suspend fun getAccountTypes(
    ) : Response<AccountTypeResponse>

    @FormUrlEncoded
    @POST("account/renew")
    suspend fun renewAccount(
       @Field("contact") contact: String,
       @Field("token") token: String,
    ) : Response<RenewAccountResponse>

    @FormUrlEncoded
    @POST("account/transaction-status")
    suspend fun transactionStatus(
       @Field("transaction_id") transactionId: String,
       @Field("token") token: String,
    ) : Response<RenewAccountResponse>

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
        @Field("school") school: String,
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
                .baseUrl("http://192.168.72.169/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MyApi::class.java)
        }
    }
}
