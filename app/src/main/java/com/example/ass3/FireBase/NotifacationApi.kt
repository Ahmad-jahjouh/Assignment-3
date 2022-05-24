package com.example.ass3

import com.example.ass3.Constants.CONTENT_TYPE
import com.example.ass3.Constants.SERVER_KEY
import com.example.ass3.FireBase.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotifiacationAPI {
    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotifiacation(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}