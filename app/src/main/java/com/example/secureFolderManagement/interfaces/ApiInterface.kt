package com.example.secureFolderManagement.interfaces

import com.example.secureFolderManagement.models.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiInterface {

    @GET("api/users")
    fun getData(): Call<List<UserResponse>>

    @POST("api/login")
    fun verifyUser(@Body UserRequest: Any?): Call<UserResponse>

    @POST("api/lockOrUnlockUser")
    fun lockUser(@Body UserLockRequest : UserLockRequest): Call<BasicResponse>

    @GET("api/lockOrUnlockUser")
    fun pollUser(@Query("username") username:String ): Call<UserPollResponse>
}