package com.pietech.bundle.data.repositories

import com.pietech.bundle.data.db.AppDatabase
import com.pietech.bundle.data.db.entities.*
import com.pietech.bundle.data.network.MyApi
import com.pietech.bundle.data.network.SafeApiRequest
import com.pietech.bundle.data.network.responses.*
import okhttp3.MultipartBody

class HomeRepository(
    private val api: MyApi,
    private val db: AppDatabase
): SafeApiRequest() {

    suspend fun index() : IndexResponse {
        return apiRequest { api.index() }
    }

    suspend fun getSimilarProducts(product_id: String) : SimilarProsuctsResponse {
        return apiRequest { api.getSimilarProducts(product_id) }
    }

    fun saveUser(user: User) = db.getUserDao().upsert(user)

    fun getUser() = db.getUserDao().getCurrentUser()

    fun getUserToken() = db.getUserDao().getUserToken()

    fun getAvatar() = db.getUserDao().getAvatar()

    suspend fun changeAvatar(image: MultipartBody.Part, data: String): AuthResponse {
        return apiRequest { api.changeAvatar(image, data) }
    }

    suspend fun changePassword(token: String, oldPassword: String, newPassword: String): DefaultResponse {
        return apiRequest { api.changePassword(token, oldPassword, newPassword) }
    }

    fun logout() = db.getUserDao().removeUser(getUser())

    fun saveProduct(product: Product) = db.getCurrentProductDao().upsert(product)

    fun getCurrentProduct() = db.getCurrentProductDao().getproduct()

    fun saveSliderImage(sliderImage: SliderImage) = db.getSliderImageDao().upsert(sliderImage)

    fun getSliderImages() = db.getSliderImageDao().getSliderImages()

    fun saveBottomAd(bottomAds: BottomAd) = db.getBottomAdsDao().upsert(bottomAds)

    fun getBottomAds() = db.getBottomAdsDao().getBottomAds()

    fun saveTopAd(topAds: TopAd) = db.getTopAdsDao().upsert(topAds)

    fun getTopAds() = db.getTopAdsDao().getTopAds()

    fun saveCategory(category: Category) = db.getCategoryDao().upsert(category)

    fun getCategory(categoryId: String) = db.getCategoryDao().getCategory(categoryId)

    fun getCategories() = db.getCategoryDao().getCategories()

    suspend fun loadCategories(token: String): CategoryResponse {
        return apiRequest { api.loadCategories(token) }
    }

    suspend fun getCategoryProduct(token: String, categoryId: String): ProductsResponse {
        return apiRequest { api.getCategoryProduct(token, categoryId) }
    }

    fun saveSubCategory(subCategory: SubCategory) = db.getSubCategoryDao().upsert(subCategory)

    fun getSubCategories(category_name: String) = db.getSubCategoryDao().getSubCategories(category_name)

    suspend fun getSubCategoryProducts(subCategoryId: String): ProductsResponse {
        return apiRequest { api.getSubCategoryProducts(subCategoryId) }
    }

    suspend fun loadSubCategories(token: String): SubCategoryResponse {
        return apiRequest { api.loadSubCategories(token) }
    }

    fun getSubCategory(subCategoryId: String) = db.getSubCategoryDao().getSubCategory(subCategoryId)

    fun saveSchool(school: School) = db.getSchoolDao().upsert(school)

    fun getSchools() = db.getSchoolDao().getSchools()

    fun getSchoolId(name: String) = db.getSchoolDao().getSchoolId(name)

    suspend fun loadSchools(): SchoolResponse {
        return apiRequest { api.loadSchools() }
    }

    suspend fun updateProfile(token: String, name: String, userName: String, contact: String, school: Int): AuthResponse {
        return apiRequest { api.updateProfile(token, name, userName, contact, school) }
    }

//    Cart
    fun addToCart(cart: Cart) = db.getCartDao().upsert(cart)

    fun removeFromCart(cart: Cart) = db.getCartDao().remove(cart)

    fun getCartItems() = db.getCartDao().getcart()

    fun getCurrentCart() = db.getCartDao().getCurrentCart()
//    Orders
    fun addToOrders(order: Order) = db.getOrderDao().upsert(order)

    fun removeFromOrders(order: Order) = db.getOrderDao().remove(order)

    fun getOrders() = db.getOrderDao().getorder()

    suspend fun getSearchItems(keyword: String) : SearchResponse {
        return apiRequest { api.search(keyword) }
    }

    suspend fun submitOrder(token: String, cart: String) : OrderResponse {
        return apiRequest { api.order(token, cart) }
    }

    suspend fun loadOrders(token: String) : OrdersResponse {
        return apiRequest { api.loadOrders(token) }
    }

//    UserProducts
    fun saveUserProduct(userProduct: UserProduct) = db.getUserProductDao().upsert(userProduct)

    fun getUserProducts() = db.getUserProductDao().getproducts()

    suspend fun getUserProducts(token: String): UserProductResponse {
        return apiRequest { api.getUserProducts(token) }
    }

}