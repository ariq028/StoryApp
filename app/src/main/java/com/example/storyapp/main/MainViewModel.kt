package com.example.storyapp.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.model.StoryRepo
import com.example.storyapp.model.UserModel
import com.example.storyapp.response.ListStoryItem
import com.example.storyapp.response.StoryResponse
import com.example.storyapp.utils.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(private val repo: StoryRepo) : ViewModel() {

    val listStories: LiveData<StoryResponse> = repo.listStory

    private val _toast = MutableLiveData<Event<String>>()
    val toast: LiveData<Event<String>> = _toast

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    val getListStories: LiveData<PagingData<ListStoryItem>> =
        repo.getStories().cachedIn(viewModelScope)

    fun getUser(): LiveData<UserModel> {
        return repo.getUser()
    }

    fun saveUser(user: UserModel) {
        CoroutineScope(Dispatchers.IO).launch {
            repo.saveUser(user)
        }
    }

    fun getStoryLocation(token: String) {
        viewModelScope.launch {
            repo.getStoryLocation(token)
        }
    }


    fun logout() {
        Log.d(TAG, "logout() function called")
        viewModelScope.launch {
            repo.logout()
        }
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}
