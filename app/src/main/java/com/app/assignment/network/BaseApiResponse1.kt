package com.app.assignment.network

abstract class BaseApiResponse1 {
    /*
        suspend fun <T> safeApiCall(apiCall: suspend () -> ResponseData<T>): NetworkResult<T> {
            return withContext(Dispatchers.IO) {
                try {
                    val response = apiCall()
                    println("response###$response")

                    if (response.status == STATUS_SUCCESS)
                        NetworkResult.Success(response.data, response.message)
                    else {
                        if (response.message == ACCESS_TOKEN_EXPIRED)
                            error(200, false, response.message)
                        //error(ACCESS_TOKEN_EXPIRED)
                        else if (response.message == REFRESH_TOKEN_EXPIRED) {
                            error(200, false, response.message)
                            //error(REFRESH_TOKEN_EXPIRED)
                        } else {
                            error(200, false, response.message)
                            //error("Api call failed ${response.status}")
                        }
                    }

                    //return error(200, false, response.message)
                    //return error(response.message)
                } catch (exception: Exception) {
                    when (exception) {
                        is HttpException -> {
                            error(exception.code(), false, exception.message())
                        }
                        is IOException -> {
                            error(
                                null,
                                true,
                                exception.message
                            )
                        }
                        else ->
                            error(
                                null,
                                true,
                                exception.message
                            )
                    }
                    /*println("response###${e.message}")
                    println("response###${e.stackTrace}")
                    return error(e.message ?: e.toString())*/
                }
            }
        }
    */
    private fun <T> error(
        errorCode: Int?,
        isNetworkError: Boolean?,
        errorBody: String?
    ): NetworkResult<T> =
        NetworkResult.Error("${errorBody.toString()}")

    /*private fun <T> error(errorMessage: String): NetworkResult<T> =
        NetworkResult.Error(errorMessage)*/

}