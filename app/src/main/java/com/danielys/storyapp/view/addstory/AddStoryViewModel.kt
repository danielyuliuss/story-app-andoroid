package com.danielys.storyapp.view.addstory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.danielys.storyapp.data.ApiConfig
import com.danielys.storyapp.data.UserPreferences
import com.danielys.storyapp.data.response.AddStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddStoryViewModel(private val userPreferences: UserPreferences) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _responseUpload = MutableLiveData<AddStoryResponse>()
    val responseUpload: LiveData<AddStoryResponse> = _responseUpload

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getToken(): LiveData<String> {
        return userPreferences.getToken().asLiveData()
    }

    fun uploadFile(token: String, imageMultipart: MultipartBody.Part, description: RequestBody) {
        _isLoading.value = true
        val authorization = "Bearer $token"
        val client =
            ApiConfig.getApiService().uploadStory(authorization, imageMultipart, description)
        client.enqueue(object : Callback<AddStoryResponse> {
            override fun onResponse(
                call: Call<AddStoryResponse>,
                response: Response<AddStoryResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _responseUpload.value = response.body()
                } else {
                    Log.e("Error", "message : ${response.message()}")
                    _errorMessage.value = response.message()
                }
            }

            override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                _isLoading.value = false
            }
        })
    }

    fun uploadFile(
        token: String,
        imageMultipart: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody,
        log: RequestBody
    ) {
        _isLoading.value = true
        val authorization = "Bearer $token"
        val client =
            ApiConfig.getApiService()
                .uploadStory(authorization, imageMultipart, description, lat, log)
        client.enqueue(object : Callback<AddStoryResponse> {
            override fun onResponse(
                call: Call<AddStoryResponse>,
                response: Response<AddStoryResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _responseUpload.value = response.body()
                } else {
                    Log.e("Error", "message : ${response.message()}")
                    _errorMessage.value = response.message()
                }
            }

            override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                _isLoading.value = false
            }
        })
    }
}