package com.example.storyapp.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.model.StoryRepo
import com.example.storyapp.model.UserModel
import com.example.storyapp.response.LoginResponse
import com.example.storyapp.utils.Event
import kotlinx.coroutines.launch

class LoginViewModel(private val repo: StoryRepo) : ViewModel() {

    val loginResponse: LiveData<LoginResponse> = repo.loginResponse
    val isLoading: LiveData<Boolean> = repo.isLoading
    val toastText: LiveData<Event<String>> = repo.toast

    fun log(email: String, password: String) {
        viewModelScope.launch {
            repo.log(email, password)
        }
    }

    fun saveUser(user: UserModel) {
        viewModelScope.launch {
            repo.saveUser(user)
        }
    }

    fun login() {
        viewModelScope.launch {
            repo.login()
        }
    }
}