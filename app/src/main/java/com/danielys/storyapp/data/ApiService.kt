package com.danielys.storyapp.data

import com.danielys.storyapp.data.response.AddStoryResponse
import com.danielys.storyapp.data.response.DetailStoryResponse
import com.danielys.storyapp.data.response.GetAllStoryResponse
import com.danielys.storyapp.data.response.LoginResponse
import com.danielys.storyapp.data.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") authorization: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): GetAllStoryResponse

    @GET("stories/{id}")
    fun getDetailStory(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): Call<DetailStoryResponse>

    @Multipart
    @POST("stories")
    fun uploadStory(
        @Header("Authorization") authorization: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Call<AddStoryResponse>

    @Multipart
    @POST("stories")
    fun uploadStory(
        @Header("Authorization") authorization: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lon") lon: RequestBody
    ): Call<AddStoryResponse>

    @GET("stories?location=1")
    fun getStoriesLocation(
        @Header("Authorization") authorization: String
    ): Call<GetAllStoryResponse>
}