package com.example.secureFolderManagement.interfaces

import com.example.secureFolderManagement.models.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiInterface {

    @GET("api/users")
    fun getData(): Call<List<UserResponse>>

    @POST("login")
    fun verifyUser(@Body UserRequest: Any?): Call<UserResponse>
}