package com.example.storyapp.main

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.storyapp.api.ApiService
import com.example.storyapp.model.UserPreference
import com.example.storyapp.response.ListStoryItem
import kotlinx.coroutines.flow.first

class StoryPagingSource(
    private val pref: UserPreference,
    private val apiService: ApiService
) : PagingSource<Int, ListStoryItem>() {

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val token = pref.getUser().first().token

            if (token.isNotEmpty()) {
                val responseData = apiService.getStories(token, position, params.loadSize)
                if (responseData.isSuccessful) {
                    LoadResult.Page(
                        data = responseData.body()?.listStory ?: emptyList(),
                        prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                        nextKey = if (responseData.body()?.listStory.isNullOrEmpty()) null else position + 1
                    )
                } else {
                    LoadResult.Error(Exception("Error"))
                }
            } else {
                LoadResult.Error(Exception("Error"))
            }
        } catch (e: Exception) {
            Log.d("Exception", "onFailure : ${e.message}")
            return LoadResult.Error(e)
        }
    }
    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}