package com.danielys.storyapp.view.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.danielys.storyapp.data.ApiConfig
import com.danielys.storyapp.data.UserPreferences
import com.danielys.storyapp.data.response.GetAllStoryResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel(private val userPreferences: UserPreferences) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _stories = MutableLiveData<GetAllStoryResponse>()
    val stories: LiveData<GetAllStoryResponse> = _stories

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getToken(): LiveData<String> {
        return userPreferences.getToken().asLiveData()
    }

    fun getStories(token: String) {
        _isLoading.value = true
        val header = "Bearer $token"
        val client = ApiConfig.getApiService().getStoriesLocation(header)
        client.enqueue(object : Callback<GetAllStoryResponse> {
            override fun onResponse(
                call: Call<GetAllStoryResponse>,
                response: Response<GetAllStoryResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _stories.value = response.body()
                } else {
                    Log.e("Error", "message : ${response.message()}")
                    _errorMessage.value = response.message()
                }
            }

            override fun onFailure(call: Call<GetAllStoryResponse>, t: Throwable) {
                _isLoading.value = false
            }
        })
    }

}