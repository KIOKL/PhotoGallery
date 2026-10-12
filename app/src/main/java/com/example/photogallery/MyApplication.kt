package com.example.photogallery

import android.app.Application
import android.util.Log
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.util.DebugLogger
import okhttp3.OkHttpClient

class MyApplication : Application(), ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader {
        val okHttp = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header(
                        "User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36"
                    )
                    .build()
                chain.proceed(request)
            }
            .build()

        return ImageLoader.Builder(this)
            .okHttpClient(okHttp)
            .logger(DebugLogger())
            .build()
    }
}