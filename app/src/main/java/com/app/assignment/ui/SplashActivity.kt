@file:Suppress("DEPRECATION")

package com.app.assignment.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.app.assignment.R
import com.app.assignment.base.AppBaseActivity
import com.app.assignment.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppBaseActivity() {

    private lateinit var binding : ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    override fun init() {

        Handler().postDelayed({
            val intent =  Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.fadein,R.anim.fadeout)
        }, 2500)

    }

    override fun setListsAndAdapters() {
        TODO("Not yet implemented")
    }

    override fun setListeners() {
        TODO("Not yet implemented")
    }

    override fun initObservers() {
        TODO("Not yet implemented")
    }

}