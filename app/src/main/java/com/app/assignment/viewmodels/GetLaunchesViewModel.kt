package com.app.assignment.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.assignment.models.LaunchesResponse
import com.app.assignment.network.NetworkResult
import com.app.assignment.repoes.GetLaunchesRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetLaunchesViewModel @Inject constructor(private val repository: GetLaunchesRepo) :
    ViewModel() {

    private val _getLaunches: MutableLiveData<NetworkResult<List<LaunchesResponse>>> = MutableLiveData()
    val getLaunches: LiveData<NetworkResult<List<LaunchesResponse>>> = _getLaunches

    fun getLaunches() = viewModelScope.launch {
        repository.getLaunches().collect { values ->
            _getLaunches.value = values
        }
    }

}