package com.bundlegh.bundle.ui.new_product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bundlegh.bundle.data.repositories.NewProductRepository
import com.bundlegh.bundle.data.repositories.UserRepository

@Suppress("UNCHECKED_CAST")
class NewProductViewModelFactory(
    private val repository: NewProductRepository
): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewProductViewModel(repository) as T
    }

}