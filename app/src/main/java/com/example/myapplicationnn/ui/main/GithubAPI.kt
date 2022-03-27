package com.example.myapplicationnn

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import java.util.*

interface GithubAPI {

    @FormUrlEncoded
    @POST("access_token")
    @Headers("Accept: application/json")
    fun getAccessToken(
        @Field("code") code: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("state") state: String,
        @Field("redirect_uri") redirectUrl: String
    ) : Call<ResponseBody>

    @FormUrlEncoded
    @POST("access_token")
    @Headers("Accept: application/json, Authorization: token ")
    fun getUser() : Call<ResponseBody>

}