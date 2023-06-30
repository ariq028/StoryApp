package com.example.storyapp.addstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.model.StoryRepo
import com.example.storyapp.model.UserModel

class AddStoryViewModel(private val repo: StoryRepo) : ViewModel() {

    fun getUser(): LiveData<UserModel> {
        return repo.getUser()
    }
}