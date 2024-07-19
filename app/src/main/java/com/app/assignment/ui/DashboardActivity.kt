@file:Suppress("DEPRECATION")

package com.app.assignment.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.assignment.R
import com.app.assignment.adapters.LaunchesAdapter
import com.app.assignment.base.AppBaseActivity
import com.app.assignment.databinding.ActivityDashboardBinding
import com.app.assignment.helpers.Parameters
import com.app.assignment.helpers.Utils
import com.app.assignment.models.LaunchesResponse
import com.app.assignment.network.NetworkResult
import com.app.assignment.viewmodels.GetLaunchesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardActivity : AppBaseActivity() {

    private lateinit var binding : ActivityDashboardBinding

    private val getLaunchesViewModel : GetLaunchesViewModel by viewModels()
    private lateinit var launchesAdapter : LaunchesAdapter
    private var launchesList = mutableListOf<LaunchesResponse>()

    private var searchTxt = ""
    private var isSearch = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        setListeners()
        setListsAndAdapters()
        initObservers()
        setSwipe()
    }


    override fun onResume() {
        super.onResume()

//        searchTxt = ""
//        isSearch = false
        fetchData()
    }

    private fun setSwipe() {

        binding.swipeFeed.setOnRefreshListener {
            fetchData()
        }

        binding.swipeFeed.post {
            binding.swipeFeed.isRefreshing = false
        }

        binding.swipeFeed.setColorSchemeResources(R.color.cp)
    }

    private fun fetchData() {
        getLaunchesViewModel.getLaunches()
    }

    override fun init() {



    }

    override fun setListsAndAdapters() {

        binding.rvLaunch.setHasFixedSize(true)
        binding.rvLaunch.isNestedScrollingEnabled = false
        binding.rvLaunch.apply {
            layoutManager =
                LinearLayoutManager(context , LinearLayoutManager.VERTICAL , false)
            launchesAdapter =
                LaunchesAdapter(this@DashboardActivity){_, _, launchesData ->
                    val intent = Intent(this@DashboardActivity,
                        DetailsActivity::class.java)
                    intent.putExtra(Parameters.launchesData , launchesData)
                    startActivity(intent)
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout)
                }
            adapter = launchesAdapter
        }

    }

    override fun setListeners() {

        binding.loutToolbar.ivTbSearch.setOnClickListener {
            circleReveal(binding.loutToolbarSearch.searchToolbar,1,true,true)
            Utils.openKeyboard(this@DashboardActivity , binding.loutToolbarSearch.etSearch)
        }


        binding.loutToolbarSearch.ivCloseToolbar.setOnClickListener {
            circleReveal(binding.loutToolbarSearch.searchToolbar,1,true,false)
            binding.loutToolbarSearch.etSearch.text!!.clear()
            Utils.closeKeyboard(this@DashboardActivity , binding.loutToolbarSearch.etSearch)

            isSearch = false
            searchTxt = ""
            fetchData()
        }

        binding.loutToolbarSearch.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if(s != null && s.isNotEmpty() && s.toString().length > 2){

                    isSearch = true

                    searchTxt = s.toString()
                    setSearchData(searchTxt)

                }else{
                    isSearch = true
                    searchTxt = ""
                    setListsAndAdapters()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }
        })


    }

    override fun initObservers() {

        getLaunchesViewModel.getLaunches.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    try {
                        Log.e("Data =>" , "Data->" + response.data.toString())

                        launchesList = mutableListOf<LaunchesResponse>()
                        val launchesModel = response.data!! as MutableList<LaunchesResponse>
                        launchesList.addAll(launchesModel)

                        if (isSearch)
                            setSearchData(searchTxt)
                        else
                            launchesAdapter.submitList(launchesList)

                    }catch (e : Exception){
                        e.printStackTrace()
                    }finally {
                        binding.swipeFeed.isRefreshing = false
                    }

                }
                is NetworkResult.Error -> {
                    //Utils.showToast(context , response.message)
                    Log.e("Data =>" ,  response.message.toString())
                }
                is NetworkResult.Loading -> {
                    // show a progress bar
                }

            }
        }

    }


    private fun setSearchData(searchTxt : String) {

        val searchList = mutableListOf<LaunchesResponse>()
        if (searchTxt.length > 2){

            try {

                for (i in 0 until launchesList.size){
                    val missionName = launchesList[i].missionName

                    if (missionName.length >= searchTxt.length){

                        val fetchName = missionName.substring(0, searchTxt.length)

                        Log.e("fullName=>" , missionName)
                        Log.e("searchTxt=>" , searchTxt)
                        if (searchTxt.toLowerCase() == fetchName.toLowerCase()){
                            Log.e("add=>" , "Added")
                            searchList.add(launchesList[i])
                        }

                    }

                }

            }catch (e : Exception){
                e.printStackTrace()
            }

            Log.e("Size=>" , searchList.size.toString())
            launchesAdapter.submitList(searchList)
            binding.rvLaunch.adapter = launchesAdapter
            launchesAdapter?.notifyDataSetChanged()

        }else{
            searchList.clear()
            setListsAndAdapters()
        }

    }


}