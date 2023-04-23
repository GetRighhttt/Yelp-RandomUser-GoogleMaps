package com.example.yelpclone.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.yelpclone.domain.repository.RepositoryImpl
import com.example.yelpclone.domain.util.DispatcherProvider

class MainViewModelFactory(
    private val repositoryImpl: RepositoryImpl,
    private val dispatchers: DispatcherProvider,
    private val app: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(repositoryImpl, dispatchers, app) as T
    }
}