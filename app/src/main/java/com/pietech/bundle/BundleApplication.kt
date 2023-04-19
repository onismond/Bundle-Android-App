package com.pietech.bundle

import android.app.Application
import com.pietech.bundle.data.db.AppDatabase
import com.pietech.bundle.data.network.MyApi
import com.pietech.bundle.data.network.NetworkConnectionInterceptor
import com.pietech.bundle.data.preferences.PreferenceProvider
import com.pietech.bundle.data.repositories.HomeRepository
import com.pietech.bundle.data.repositories.NewProductRepository
import com.pietech.bundle.data.repositories.UserRepository
import com.pietech.bundle.ui.auth.AuthViewModelFactory
import com.pietech.bundle.ui.home.HomeViewModelFactory
import com.pietech.bundle.ui.new_product.NewProductViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class BundleApplication: Application(), KodeinAware {

    override val kodein = Kodein.lazy {
        import(androidXModule(this@BundleApplication))

        bind() from singleton { NetworkConnectionInterceptor(instance()) }
        bind() from singleton { MyApi(instance()) }
        bind() from singleton { AppDatabase(instance()) }
        bind() from singleton { PreferenceProvider(instance()) }
        bind() from singleton { UserRepository(instance(), instance()) }
        bind() from singleton { HomeRepository(instance(), instance()) }
        bind() from singleton { NewProductRepository(instance(), instance()) }
        bind() from provider { AuthViewModelFactory(instance()) }
        bind() from provider { HomeViewModelFactory(instance()) }
        bind() from provider { NewProductViewModelFactory(instance()) }
    }
}