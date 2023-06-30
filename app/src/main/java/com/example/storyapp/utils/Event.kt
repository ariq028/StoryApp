package com.example.storyapp.utils

open class Event<out T>(private val content: T) {

    @Suppress("PrivateMemberVisibility")
    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }
}