package com.app.assignment.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.assignment.R
import com.app.assignment.databinding.DesignLaunchListBinding
import com.app.assignment.models.LaunchesResponse
import com.bumptech.glide.Glide

@Suppress("DEPRECATION")
class LaunchesAdapter (private val context: Context,
                       private val listener: (items: MutableList<LaunchesResponse>, position : Int,
                                              launchesResponse: LaunchesResponse) -> Unit) :
    ListAdapter<LaunchesResponse, LaunchesAdapter.CustomerViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val binding =
            DesignLaunchListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CustomerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    override fun onBindViewHolder(holder: LaunchesAdapter.CustomerViewHolder, pos: Int) {
        val currentItem = getItem(pos)
        holder.bind(currentItem)
    }

    inner class CustomerViewHolder(private val binding: DesignLaunchListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LaunchesResponse) {

            binding.apply {

                tvMissionName.text = item.missionName
                tvLaunchYear.text = item.launchYear
                tvRocketName.text = item.rocket.rocketName

                if (item.links.missionPatch != null){
                    Glide.with(context)
                        .load(item.links.missionPatch)
                        .placeholder(R.drawable.image_placeholder)
                        .into(binding.ivImg)
                }else{
                    Glide.with(context)
                        .load(R.drawable.image_placeholder)
                        .placeholder(R.drawable.image_placeholder)
                        .into(binding.ivImg)
                }

                binding.loutMain.setOnClickListener {
                    listener.invoke( currentList , position , item)
                }

            }

        }


    }

    class DiffCallback : DiffUtil.ItemCallback<LaunchesResponse>() {
        override fun areItemsTheSame(old: LaunchesResponse, aNew: LaunchesResponse) =
            old.flightNumber == aNew.flightNumber

        override fun areContentsTheSame(old: LaunchesResponse, aNew: LaunchesResponse) =
            old == aNew

    }

}