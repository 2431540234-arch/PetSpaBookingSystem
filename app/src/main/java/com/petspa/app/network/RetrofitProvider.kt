package com.petspa.app.network

import android.content.Context
import com.petspa.app.data.TokenDataStore
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.firstOrNull
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitProvider {

    // Base URL for Android emulator to localhost backend
    private const val BASE_URL = "http://10.0.2.2:8080/api/"

    fun createApi(context: Context): ApiService {
        val tokenStore = TokenDataStore(context)

        val authInterceptor = Interceptor { chain ->
            val original: Request = chain.request()
            val builder = original.newBuilder()

            // Blocking read to get current token (small cost at network call time)
            val token = runBlocking { tokenStore.authFlow.firstOrNull()?.token }
            if (!token.isNullOrEmpty()) {
                builder.addHeader("Authorization", "Bearer $token")
            }
            builder.addHeader("Content-Type", "application/json")
            chain.proceed(builder.build())
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}

