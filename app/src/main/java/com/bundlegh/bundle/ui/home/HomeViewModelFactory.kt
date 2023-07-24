package com.bundlegh.bundle.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bundlegh.bundle.data.repositories.HomeRepository

@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory(
    private val repository: HomeRepository
): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repository) as T
    }

}