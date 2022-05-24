package com.example.ass3.FireBase

import com.example.ass3.Constants.BASE_URL
import com.example.ass3.NotifiacationAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitIInstance {

    companion object{
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        val api by lazy {
            retrofit.create(NotifiacationAPI::class.java )
        }
    }
}