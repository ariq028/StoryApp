package com.example.storyapp.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.model.StoryRepo
import com.example.storyapp.response.RegisterResponse
import com.example.storyapp.utils.Event
import kotlinx.coroutines.launch

class RegisterViewModel(private val repo: StoryRepo) : ViewModel() {

    val registerResponse: LiveData<RegisterResponse> = repo.registerResponse
    val isLoading: LiveData<Boolean> = repo.isLoading
    val toast: LiveData<Event<String>> = repo.toast

    fun regist(name: String, email: String, password: String) {
        viewModelScope.launch {
            repo.regist(name, email, password)
        }
    }
}