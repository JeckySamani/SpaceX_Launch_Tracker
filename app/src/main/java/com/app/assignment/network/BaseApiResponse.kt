package com.app.assignment.network

import android.util.Log
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

abstract class BaseApiResponse {
    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
        try {
            val response = apiCall()
            Log.e("Data => " , response.toString())
            if (response.isSuccessful) {
                val body = response.body()
                body?.let {
                    return NetworkResult.Success(body)
                }

            }
            return error(response.code(), false, response.message())
        } catch (exception: Exception) {

            when (exception) {
                is HttpException -> {
                    return error(exception.code(), false, exception.message())
                }
                is IOException -> {
                    return error(
                        null,
                        true,
                        exception.message
                    )
                }
                else ->
                    return error(
                        null,
                        true,
                        exception.message
                    )
            }

            //return error(e.message ?: e.toString())
        }
    }

    private fun <T> error(
        errorCode: Int?,
        isNetworkError: Boolean?,
        errorBody: String?
    ): NetworkResult<T> =
        NetworkResult.Error(errorBody.toString())
    // NetworkResult.Error("Api call failed ${errorBody.toString()}")
}