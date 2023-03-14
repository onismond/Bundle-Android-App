package com.pietech.bundle.data.repositories

import com.pietech.bundle.data.db.AppDatabase
import com.pietech.bundle.data.db.entities.Cart
import com.pietech.bundle.data.db.entities.Order
import com.pietech.bundle.data.db.entities.Product
import com.pietech.bundle.data.db.entities.User
import com.pietech.bundle.data.network.MyApi
import com.pietech.bundle.data.network.SafeApiRequest
import com.pietech.bundle.data.network.responses.AuthResponse
import com.pietech.bundle.data.network.responses.IndexResponse
import com.pietech.bundle.data.network.responses.OrderResponse
import com.pietech.bundle.data.network.responses.SearchResponse

class HomeRepository(
    private val api: MyApi,
    private val db: AppDatabase
): SafeApiRequest() {

    suspend fun index() : IndexResponse {
        return apiRequest { api.index() }
    }

    fun getUser() = db.getUserDao().getCurrentUser()

    fun getUserToken() = db.getUserDao().getUserToken()

    fun saveProduct(product: Product) = db.getCurrentProductDao().upsert(product)

    fun getCurrentProduct() = db.getCurrentProductDao().getproduct()
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



//    suspend fun userLogin(email: String, password: String) : AuthResponse {
//        return apiRequest { api.userLogin(email, password) }
//    }
//
//    suspend fun userSignup(
//        name: String,
//        username: String,
//        email: String,
//        password: String
//    ): AuthResponse {
//        return apiRequest { api.userSignup(name, username, email, password) }
//    }
//
//    fun saveUser(user: User) = db.getUserDao().upsert(user)
//
//    fun getUser() = db.getUserDao().getuser()
}