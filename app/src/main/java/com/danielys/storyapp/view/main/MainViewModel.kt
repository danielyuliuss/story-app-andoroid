package com.danielys.storyapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.danielys.storyapp.data.ApiConfig
import com.danielys.storyapp.data.StoryPagingSource
import com.danielys.storyapp.data.UserPreferences
import com.danielys.storyapp.data.response.ListStoryItem
import kotlinx.coroutines.launch

class MainViewModel(private val userPreferences: UserPreferences) : ViewModel() {

    var tokenInput: String = "salah"
    var storiesData: LiveData<PagingData<ListStoryItem>> = getStories().cachedIn(viewModelScope)


    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(ApiConfig.getApiService(), "Bearer $tokenInput")
            }
        ).liveData
    }

    fun getToken(): LiveData<String> {
        return userPreferences.getToken().asLiveData()
    }

    fun setName(name: String) {
        viewModelScope.launch {
            userPreferences.setName(name)
        }
    }

    fun setUserId(userid: String) {
        viewModelScope.launch {
            userPreferences.setUserId(userid)
        }
    }

    fun setToken(token: String) {
        viewModelScope.launch {
            userPreferences.setToken(token)
        }
    }

}