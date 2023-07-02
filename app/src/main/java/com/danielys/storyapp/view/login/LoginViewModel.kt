package com.danielys.storyapp.view.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danielys.storyapp.data.ApiConfig
import com.danielys.storyapp.data.UserPreferences
import com.danielys.storyapp.data.response.LoginResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val userPreferences: UserPreferences) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _dataLogin = MutableLiveData<LoginResponse>()
    val dataLogin: LiveData<LoginResponse> = _dataLogin

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun login(email: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _dataLogin.value = response.body()
                } else {
                    Log.e("Error", "message : ${response.message()}")
                    _errorMessage.value = response.message()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
            }

        })
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