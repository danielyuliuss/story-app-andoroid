package com.danielys.storyapp.view.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.danielys.storyapp.data.ApiConfig
import com.danielys.storyapp.data.UserPreferences
import com.danielys.storyapp.data.response.DetailStoryResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel(private val userPreferences: UserPreferences) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _dataDetail = MutableLiveData<DetailStoryResponse>()
    val dataDetail: LiveData<DetailStoryResponse> = _dataDetail

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getToken(): LiveData<String> {
        return userPreferences.getToken().asLiveData()
    }

    fun getDetail(token: String, id: String) {
        _isLoading.value = true
        val header = "Bearer $token"
        val client = ApiConfig.getApiService().getDetailStory(header, id)
        client.enqueue(object : Callback<DetailStoryResponse> {
            override fun onResponse(
                call: Call<DetailStoryResponse>,
                response: Response<DetailStoryResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _dataDetail.value = response.body()
                } else {
                    Log.e("Error", "message : ${response.message()}")
                    _errorMessage.value = response.message()
                }
            }

            override fun onFailure(call: Call<DetailStoryResponse>, t: Throwable) {
                _isLoading.value = false
            }

        })
    }
}