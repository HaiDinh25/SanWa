package com.sanwashoseki.bookskozuchi.services

import com.sanwashoseki.bookskozuchi.others.Const
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val TIMEOUT_MINUTES = 1L
    private const val MAX_IDLE_CONNECTIONS = 32
    private const val CONNECTION_ALIVE = 1L

    private var mAPIService: APIService? = null
    private var mOkHttpClient: OkHttpClient? = null

    @get:Synchronized
    private val okHttpClient: OkHttpClient?
        get() {
            if (mOkHttpClient == null) {
                val interceptor = HttpLoggingInterceptor()
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                val builder = OkHttpClient.Builder()
                builder.addInterceptor(interceptor)
                    .callTimeout(TIMEOUT_MINUTES, TimeUnit.MINUTES)
                    .connectTimeout(TIMEOUT_MINUTES, TimeUnit.MINUTES)
                    .readTimeout(TIMEOUT_MINUTES, TimeUnit.MINUTES)
                    .writeTimeout(TIMEOUT_MINUTES, TimeUnit.MINUTES)
                    .connectionPool(ConnectionPool(MAX_IDLE_CONNECTIONS, CONNECTION_ALIVE, TimeUnit.MINUTES))
                mOkHttpClient = builder.build()
            }
            return mOkHttpClient
        }

    @get:Synchronized
    val client: APIService?
        get() {
            if (null == mAPIService) {
                mAPIService = Retrofit.Builder()
                    .baseUrl(Const.BASE_URL)
                    .client(okHttpClient)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(APIService::class.java)
            }
            return mAPIService
        }
}