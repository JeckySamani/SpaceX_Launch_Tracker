package com.app.assignment.network

import javax.inject.Inject

class RemoteDataSource  @Inject constructor(private val apiService: ApiService) {
    suspend fun getLaunches() = apiService.getLaunches()

}