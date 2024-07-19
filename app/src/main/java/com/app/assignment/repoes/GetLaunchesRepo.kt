package com.app.assignment.repoes

import com.app.assignment.models.LaunchesResponse
import com.app.assignment.network.BaseApiResponse
import com.app.assignment.network.RemoteDataSource
import com.app.assignment.network.NetworkResult
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@ActivityRetainedScoped
class GetLaunchesRepo @Inject constructor(private val remoteDataSource: RemoteDataSource) :
    BaseApiResponse(){

//    suspend fun getImages(clientId: String, perPage: String, page: String): Flow<NetworkResult<List<ImageModel>>> {
//        return flow<NetworkResult<List<ImageModel>>> {
//            emit(safeApiCall { remoteDataSource.getImages(clientId, perPage, page) })
//        }.flowOn(Dispatchers.IO)
//    }



    suspend fun getLaunches(): Flow<NetworkResult<List<LaunchesResponse>>> {
        return flow {
            emit(NetworkResult.Loading()) // Emit loading state
            val response = remoteDataSource.getLaunches()
            if (response.isSuccessful) {
                val images = response.body() ?: emptyList()
                emit(NetworkResult.Success(images))
            } else {
                emit(NetworkResult.Error("Failed to fetch images"))
            }
        }.flowOn(Dispatchers.IO)
    }

}