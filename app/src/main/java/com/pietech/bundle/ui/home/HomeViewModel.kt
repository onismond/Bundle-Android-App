package com.pietech.bundle.ui.home

import androidx.lifecycle.ViewModel
import com.pietech.bundle.data.db.entities.Cart
import com.pietech.bundle.data.db.entities.Order
import com.pietech.bundle.data.db.entities.Product
import com.pietech.bundle.data.db.entities.User
import com.pietech.bundle.data.network.responses.OrderResponse
import com.pietech.bundle.data.repositories.HomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val repository: HomeRepository
) : ViewModel() {

    suspend fun getIndexProducts() = withContext(Dispatchers.IO){
        repository.index()
    }

    fun getLoggedInUser() = repository.getUser()

    fun getUserToken() = repository.getUserToken()

    fun getCurrentProduct() = repository.getCurrentProduct()

    fun saveProduct(product: Product) = repository.saveProduct(product)
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