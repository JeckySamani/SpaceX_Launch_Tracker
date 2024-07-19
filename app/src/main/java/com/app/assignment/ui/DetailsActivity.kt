package com.app.assignment.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.method.LinkMovementMethod
import android.view.View
import com.app.assignment.R
import com.app.assignment.base.AppBaseActivity
import com.app.assignment.databinding.ActivityDetailsBinding
import com.app.assignment.helpers.Parameters
import com.app.assignment.helpers.Utils
import com.app.assignment.models.LaunchesResponse
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@AndroidEntryPoint
class DetailsActivity : AppBaseActivity() {

    private lateinit var binding : ActivityDetailsBinding

    private var launchesData: LaunchesResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        setListeners()
    }

    override fun init() {

        binding.loutToolbar.ivTbBack.visibility = View.VISIBLE
        binding.loutToolbar.ivTbSearch.visibility = View.INVISIBLE
        binding.loutToolbar.ivIcon.visibility = View.INVISIBLE

        if (intent.hasExtra(Parameters.launchesData)) {
            launchesData = intent.getParcelableExtra<LaunchesResponse>(Parameters.launchesData)
        }

        if (launchesData != null)
            setData()

    }

    @SuppressLint("SetTextI18n")
    private fun setData() {

        Glide.with(this)
            .load(launchesData!!.links.missionPatch)
            .placeholder(R.drawable.image_placeholder)
            .into(binding.ivImg)

        binding.tvMissionName.text = launchesData!!.missionName + " " +
                "(" + launchesData!!.launchYear + ")"

        binding.tvRocketName.text = launchesData!!.rocket.rocketName
        binding.tvRocketType.text = launchesData!!.rocket.rocketType

        binding.tvPayloadId.text = launchesData!!.rocket.secondStage.payloadsList?.get(0)!!.payloadId
        binding.tvPayloadType.text = launchesData!!.rocket.secondStage.payloadsList?.get(0)!!.payloadType
        binding.tvReused.text = launchesData!!.rocket.secondStage.payloadsList?.get(0)!!.reused.toString()
        binding.tvManufacturer.text = launchesData!!.rocket.secondStage.payloadsList?.get(0)!!.manufacturer
        binding.tvNationality.text = launchesData!!.rocket.secondStage.payloadsList?.get(0)!!.nationality

        binding.tvLaunchSite.text = launchesData!!.launchSite.siteNameLong

        binding.tvArticleLink.apply {
            text = launchesData!!.links.articleLink
            // Set movement method to make the TextView act like a link
            movementMethod = LinkMovementMethod.getInstance()
            // Set an underline
            paint.isUnderlineText = true
            setOnClickListener {
                openLink(launchesData!!.links.articleLink) // Replace with your URL or handle null case
            }
        }

        binding.tvWikipedia.apply {
            text = launchesData!!.links.wikipedia
            // Set movement method to make the TextView act like a link
            movementMethod = LinkMovementMethod.getInstance()
            // Set an underline
            paint.isUnderlineText = true
            setOnClickListener {
                openLink(launchesData!!.links.wikipedia) // Replace with your URL or handle null case
            }
        }

        if (launchesData!!.links.videoLink == null || launchesData!!.links.videoLink!!.isEmpty()){
            binding.cvWatchVideo.visibility = View.GONE
        }

        binding.loutWatchVideo.setOnClickListener {
            openLink(launchesData!!.links.videoLink)
        }

    }

    private fun openLink(url: String?) {
        if (url.isNullOrEmpty()) {
            Utils.showToast(this, "Invalid URL")
        } else {
            val intent = Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) }
            startActivity(intent)
        }
    }

    override fun setListsAndAdapters() {
        TODO("Not yet implemented")
    }

    override fun setListeners() {

        binding.loutToolbar.ivTbBack.setOnClickListener {
            onBackPressed()
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        }


    }

    override fun initObservers() {
        TODO("Not yet implemented")
    }

}