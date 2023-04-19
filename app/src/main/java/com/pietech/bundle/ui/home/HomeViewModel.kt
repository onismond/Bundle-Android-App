package com.pietech.bundle.ui.home

import androidx.lifecycle.ViewModel
import com.pietech.bundle.data.db.entities.*
import com.pietech.bundle.data.repositories.HomeRepository
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

    suspend fun saveLoggedInUser(user: User) = repository.saveUser(user)

    fun getLoggedInUser() = repository.getUser()

    fun logout() = repository.logout()

    fun getUserToken() = repository.getUserToken()

    fun getAvatar() = repository.getAvatar()

    suspend fun changeAvatar(image: MultipartBody.Part, data: String) = withContext(Dispatchers.IO){
        repository.changeAvatar(image, data)
    }

    suspend fun changePassword(oldPassword: String, newPassword: String) = withContext(Dispatchers.IO){
        repository.changePassword(getUserToken(), oldPassword, newPassword)
    }

    fun getCurrentProduct() = repository.getCurrentProduct()

    fun saveProduct(product: Product) = repository.saveProduct(product)

    fun saveSliderImage(sliderImage: SliderImage) = repository.saveSliderImage(sliderImage)

    fun getSliderImages() = repository.getSliderImages()

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

    suspend fun updateProfile(name: String, userName: String, contact: String, school: Int) = withContext(Dispatchers.IO){
        repository.updateProfile(getUserToken(), name, userName, contact, school)
    }

//    Cart
    suspend fun addToCart(cart: Cart) = withContext(Dispatchers.IO){
        repository.addToCart(cart)
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

    suspend fun submitOrder(cart: String) = repository.submitOrder(getUserToken(), cart)

    suspend fun loadOrders() = repository.loadOrders(getUserToken())

    fun saveUserProduct(userProduct: UserProduct) = repository.saveUserProduct(userProduct)

    fun getUserProducts() = repository.getUserProducts()

    suspend fun geUserProducts() = withContext(Dispatchers.IO){
        repository.getUserProducts(getUserToken())
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