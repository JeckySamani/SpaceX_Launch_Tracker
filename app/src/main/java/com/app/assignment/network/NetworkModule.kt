package com.app.assignment.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    /*@Singleton
   @Provides
   fun provideHttpClient(): OkHttpClient {
       return OkHttpClient
           .Builder()
           .readTimeout(15, TimeUnit.SECONDS)
           .connectTimeout(15, TimeUnit.SECONDS)
           .build()
   }*/

    @Provides
    @Named("baseUrl")
    fun provideBaseUrl() = NetworkConstants.BASE_URL

    @Provides
    @Singleton
    fun provideOkHttpClient(/*sharedPrefManager: SharedPrefManager*/): OkHttpClient /* if (BuildConfig.DEBUG) */ {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            this.setLevel(HttpLoggingInterceptor.Level.BODY)
        }


//        Log.e("User token =>" ,sharedPrefManager.getString("user_token").toString() )

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
//                    .addHeader("token", sharedPrefManager.getString("user_token").toString())
                    //.addHeader("token", sharedPrefManager.getUserToken().toString())
                    //.addHeader("token", sharedPrefManager.getUserToken().toString())
//                    .addHeader("Authorization", "Bearer ${sharedPrefManager.getString("token")}")
//                    .addHeader("Accesskey" , "4848")
                    .build()
                chain.proceed(newRequest)
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }  /*else OkHttpClient
        .Builder()
        .build()*/

    @Singleton
    @Provides
    fun provideConverterFactory(): GsonConverterFactory =
        GsonConverterFactory.create()

    @Singleton
    @Provides
    fun providesRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory, @Named("baseUrl") BASE_URL: String
    ): Retrofit {
        return Retrofit.Builder().baseUrl("${BASE_URL}")
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

}