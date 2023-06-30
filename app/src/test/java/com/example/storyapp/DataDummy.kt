package com.example.storyapp

import com.example.storyapp.response.ListStoryItem

object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                i.toString(),
                "author + $i",
                "name $i",
                "description $i",
                112.80571,
                -7.2920184,
                "$i"
            )
            items.add(story)
        }
        return items
    }
}