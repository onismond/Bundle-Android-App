package com.bundlegh.bundle.ui.home

import androidx.lifecycle.ViewModel
import com.bundlegh.bundle.data.db.entities.*
import com.bundlegh.bundle.data.repositories.HomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody

class HomeViewModel(
    private val repository: HomeRepository
) : ViewModel() {

    suspend fun getIndexProducts() = withContext(Dispatchers.IO){
        repository.index()
    }

    suspend fun getSimilarProducts(product_id: String) = withContext(Dispatchers.IO){
        repository.getSimilarProducts(product_id)
    }

    fun getProfile() = repository.getProfile()

    fun saveProfile(profile: Profile) = repository.saveProfile(profile)

    suspend fun loadProfile() = withContext(Dispatchers.IO){
        repository.loadProfile(getUserToken())
    }

    fun saveLoggedInUser(user: User) = repository.saveUser(user)

    fun getLoggedInUser() = repository.getUser()

    fun logout() {
        repository.removeUser()
        repository.removeOrders()
        repository.removeUserProducts()
    }

    fun getUserToken() = repository.getUserToken()

    fun getAvatar() = repository.getAvatar()

    suspend fun changeAvatar(image: MultipartBody.Part, data: String) = withContext(Dispatchers.IO){
        repository.changeAvatar(image, data)
    }

    suspend fun changePassword(oldPassword: String, newPassword: String) = withContext(Dispatchers.IO){
        repository.changePassword(getUserToken(), oldPassword, newPassword)
    }

    suspend fun updateUserProduct(id: String, name: String, amount: String, description: String) = withContext(Dispatchers.IO){
        repository.updateUserProduct(getUserToken(), id, name, amount, description)
    }

    suspend fun deleteUserProduct(productId: String) = withContext(Dispatchers.IO){
        repository.deleteUserProduct(getUserToken(), productId)
    }

    fun removeUserProduct(userProduct: UserProduct) = repository.removeUserProduct(userProduct)

    fun getUserProduct(productId: String) = repository.getUserProduct(productId)

    fun getCurrentProduct() = repository.getCurrentProduct()

    fun saveProduct(product: Product) = repository.saveProduct(product)

    fun saveSliderImage(sliderImage: SliderImage) = repository.saveSliderImage(sliderImage)

    fun getSliderImages() = repository.getSliderImages()

    suspend fun getProductImages(productId: String) = withContext(Dispatchers.IO){
        repository.getProductImages(productId)
    }

    suspend fun getStoreProducts(storeId: String) = withContext(Dispatchers.IO){
        repository.getStoreProducts(storeId)
    }

    fun saveBottomAd(bottomAds: BottomAd) = repository.saveBottomAd(bottomAds)

    fun getBottomAds() = repository.getBottomAds()

    fun saveTopAd(topAds: TopAd) = repository.saveTopAd(topAds)

    fun getTopAds() = repository.getTopAds()

    fun saveCategory(category: Category) = repository.saveCategory(category)

    fun getCategory(categoryId: String) = repository.getCategory(categoryId)

    fun getCategories() = repository.getCategories()

    suspend fun loadCategories() = withContext(Dispatchers.IO){
        repository.loadCategories(getUserToken())
    }

    suspend fun getCategoryProduct(categoryId: String) = withContext(Dispatchers.IO){
        repository.getCategoryProduct(getUserToken(), categoryId)
    }

    suspend fun getSubCategoryProducts(subCategoryId: String) = withContext(Dispatchers.IO){
        repository.getSubCategoryProducts(subCategoryId)
    }

    fun getSubCategory(subCategoryId: String) = repository.getSubCategory(subCategoryId)

    fun saveSubCategory(subCategory: SubCategory) = repository.saveSubCategory(subCategory)

    fun getSubCategories(category_name: String) = repository.getSubCategories(category_name)

    suspend fun loadSubCategories() = withContext(Dispatchers.IO){
        repository.loadSubCategories(getUserToken())
    }

    fun saveSchool(school: School) = repository.saveSchool(school)

    fun getSchools() = repository.getSchools()

    fun getSchoolId(name: String) = repository.getSchoolId(name)

    suspend fun loadSchools() = withContext(Dispatchers.IO){
        repository.loadSchools()
    }

    suspend fun updateProfile(name: String, userName: String, contact: String) = withContext(Dispatchers.IO){
        repository.updateProfile(getUserToken(), name, userName, contact)
    }

//    Cart
    suspend fun addToCart(cart: Cart) = withContext(Dispatchers.IO){
        repository.addToCart(cart)
    }

    suspend fun updateCart(cart: Cart) = withContext(Dispatchers.IO){
        repository.updateCart(cart)
    }

    suspend fun removeFromCart(cart: Cart) = withContext(Dispatchers.IO){
        repository.removeFromCart(cart)
    }

    fun getCartItems() = repository.getCartItems()

    fun getCurrentCart() = repository.getCurrentCart()

    suspend fun getSearchItems(keyword: String) = withContext(Dispatchers.IO) {
        repository.getSearchItems(keyword)
    }
//    Orders
    suspend fun addToOrders(order: Order) = withContext(Dispatchers.IO){
        repository.addToOrders(order)
    }

    suspend fun removeFromOrders(order: Order) = withContext(Dispatchers.IO){
        repository.removeFromOrders(order)
    }

    fun getOrders() = repository.getOrders()

    suspend fun submitOrder(cart: String, deliveryLocation: String, contact: String, startTime: String, endTime: String) =
        repository.submitOrder(
            getUserToken(), cart, deliveryLocation, contact, startTime, endTime
        )

    suspend fun loadOrders() = repository.loadOrders(getUserToken())

    fun saveUserProduct(userProduct: UserProduct) = repository.saveUserProduct(userProduct)

    fun getUserProducts() = repository.getUserProducts()

    suspend fun getProduct(productId: String) = withContext(Dispatchers.IO){
        repository.getProduct(productId)
    }

    suspend fun getStore(storeId: String) = withContext(Dispatchers.IO){
        repository.getStore(storeId)
    }

    suspend fun geUserProducts() = withContext(Dispatchers.IO){
        repository.getUserProducts(getUserToken())
    }

    suspend fun getAccountTypes() = withContext(Dispatchers.IO){
        repository.getAccountTypes()
    }

    suspend fun renewAccount(contact: String) = withContext(Dispatchers.IO){
        repository.renewAccount(contact, getUserToken())
    }

    suspend fun transactionStatus(transactionId: String) = withContext(Dispatchers.IO){
        repository.transactionStatus(transactionId, getUserToken())
    }

    fun formatTime(time: String): String {
        try {
            val fTime = time.toInt()
            if (fTime > 12)
                return "${fTime - 12} pm"
            return "$fTime am"
        } catch (e: Exception) {
            return time
        }
    }


//    fun getFeaturedProducts(): MutableList<Product> {
//        val product = Product("", "Big Camera", "5000.00", "", "UCC")
//        val products: MutableList<Product> = mutableListOf()
//        for (i in 1..3) {
//            products.add(product)
//        }
//        return products
//    }
//
//    fun getNewlyAddedProducts(): MutableList<Product> {
//        val product = Product("", "Big Camera", "5000.00", "", "UCC")
//        val products: MutableList<Product> = mutableListOf(product)
//        return products
//    }
//
//    fun getPopularProducts(): MutableList<Product> {
//        val product = Product("", "Big Camera", "5000.00", "", "UCC")
//        val products: MutableList<Product> = mutableListOf(product)
//        return products
//    }

}