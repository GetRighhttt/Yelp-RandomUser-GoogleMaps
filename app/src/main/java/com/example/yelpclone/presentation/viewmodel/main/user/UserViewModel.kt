package com.example.yelpclone.presentation.viewmodel.main.user

import android.icu.lang.UCharacter.GraphemeClusterBreak.V
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.yelpclone.core.events.Resource
import com.example.yelpclone.core.events.SearchEvent
import com.example.yelpclone.core.util.DispatcherProvider
import com.example.yelpclone.data.model.users.UserList
import com.example.yelpclone.domain.RepositoryImpl
import com.example.yelpclone.domain.UserRepositoryImpl
import com.example.yelpclone.presentation.viewmodel.main.MainViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repositoryImpl: UserRepositoryImpl,
    private val dispatcherProvider: DispatcherProvider
): ViewModel() {

    private val _userState: MutableStateFlow<SearchEvent<List<UserList?>>> = MutableStateFlow(SearchEvent.Idle())
    val userState: MutableStateFlow<SearchEvent<List<UserList?>>> get() = _userState

    companion object {
        private const val USER_VIEW_MODEL = "USER_VIEW_MODEL"
    }

    init {
        _userState.value = SearchEvent.Loading()
        Log.d(
            USER_VIEW_MODEL,
            "User View Model is initialized. getRestaurants() method has been called. ")
        getUsers()
    }

    fun getUsers() = viewModelScope.launch(dispatcherProvider.ioCD) {
        delay(1000)

        try{
            when(val result = repositoryImpl.getUsers()) {
                is Resource.Error -> {
                    _userState.value = SearchEvent.Failure(result.message.toString())
                    Log.d(USER_VIEW_MODEL, "FAILED to find data: ${result.message}")
                }
                is Resource.Loading -> {
                    _userState.value = SearchEvent.Loading()
                    Log.d(USER_VIEW_MODEL, "Loading restaurants.")

                }
                is Resource.Success -> {
                    _userState.value = result.data?.let { SearchEvent.Success(it) }!!
                    Log.d(USER_VIEW_MODEL, "SUCCESSFULLY found data! : ${result.data.toList()}")

                }
            }
        } catch (e: Exception) {
            Log.e(USER_VIEW_MODEL, "Error getting users!", e)
        }
    }
}
