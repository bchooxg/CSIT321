package com.SFM.secureFolderManagement.interfaces

import com.SFM.secureFolderManagement.models.*
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {

    @GET("api/users/{username}")
    fun pollUserDetails( @Path("username") username: String): Call<UserResponse>

    @POST("api/login")
    fun verifyUser(@Body UserRequest: Any?): Call<UserResponse>

    @POST("api/lockOrUnlockUser")
    fun lockUser(@Body UserLockRequest : UserLockRequest): Call<BasicResponse>

    @GET("api/lockOrUnlockUser")
    fun pollUser(@Query("username") username:String ): Call<UserPollResponse>

    @POST("api/addLog")
    fun sendLogs(@Body LogRequest: LogRequest): Call<BasicResponse>
}