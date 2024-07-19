package com.app.assignment.network

import com.app.assignment.models.LaunchesResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET("launches/")
    suspend fun getLaunches(): Response<List<LaunchesResponse>>

}