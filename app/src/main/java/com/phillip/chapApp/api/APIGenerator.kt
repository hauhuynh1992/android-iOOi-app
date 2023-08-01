package com.phillip.chapApp.api


import android.util.Log
import com.phillip.chapApp.helper.Config
import com.phillip.chapApp.helper.PreferencesHelper
import com.phillip.chapApp.ui.base.SocialApp
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class APIGenerator(baseUrl: String) {
    private var logging: HttpLoggingInterceptor = HttpLoggingInterceptor()
    private var httpClient: OkHttpClient.Builder = OkHttpClient.Builder()

    private var builder: Retrofit.Builder? = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())

    fun <S> createService(serviceClass: Class<S>): S {
        logging.level = HttpLoggingInterceptor.Level.BODY
        httpClient.readTimeout(5, TimeUnit.SECONDS)
        httpClient.connectTimeout(5, TimeUnit.SECONDS)
        httpClient.addNetworkInterceptor { chain ->
            var request = chain.request()
            var httpUrl = request.url()
            val builder = request.newBuilder().url(httpUrl)
            builder.addHeader("Authorization", "Bearer " + SocialApp.getAppInstance().getPref().token)
            builder.addHeader("Content-Type", "application/json")
            request = builder.build()
            chain.proceed(request)
        }.interceptors().add(logging)
        Log.d("AAA", "Bearer " + SocialApp.getAppInstance().getPref().token)
        builder!!.client(httpClient.build())
        val retrofit = builder!!.build()
        return retrofit.create(serviceClass)
    }

    fun getRetrofit(): Retrofit? {
        return if (builder == null) {
            null
        } else builder!!.build()
    }
}
