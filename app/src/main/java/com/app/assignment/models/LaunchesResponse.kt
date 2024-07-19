package com.app.assignment.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class LaunchesResponse (
    @SerializedName("flight_number") var flightNumber : Int = 0,
    @SerializedName("mission_name") var missionName : String = "",
    @SerializedName("launch_year") var launchYear : String = "",
    @SerializedName("rocket") var rocket: Rocket = Rocket(),
    @SerializedName("launch_site") var launchSite: LaunchSite = LaunchSite(),
    @SerializedName("launch_success") var launchSuccess : Boolean = false,
    @SerializedName("launch_failure_details") var launchFailureDetails: LaunchFailureDetails = LaunchFailureDetails(),
    @SerializedName("links") var links: Links = Links(),
): Parcelable {

    @Serializable
    @Parcelize
    data class Rocket(
        @SerializedName("rocket_id") var rocketId: String = "",
        @SerializedName("rocket_name") var rocketName: String = "",
        @SerializedName("rocket_type") var rocketType: String = "",
        @SerializedName("second_stage") var secondStage: SecondStage = SecondStage(),
    ) : Parcelable{

        @Serializable
        @Parcelize
        data class SecondStage(
            @SerializedName("block") var block: Int? = null,
            @SerializedName("payloads") val payloadsList: List<PayloadsList>? = null,
        ) : Parcelable{

            @Serializable
            @Parcelize
            data class PayloadsList(
                @SerializedName("payload_id") var payloadId: String? = null,
                @SerializedName("reused") var reused: Boolean = false,
                @SerializedName("nationality") var nationality: String? = null,
                @SerializedName("manufacturer") var manufacturer: String? = null,
                @SerializedName("payload_type") var payloadType: String? = null,
            ) : Parcelable

        }

    }

    @Serializable
    @Parcelize
    data class LaunchSite(
        @SerializedName("site_id") var siteId: String = "",
        @SerializedName("site_name") var siteName: String = "",
        @SerializedName("site_name_long") var siteNameLong: String = "",
    ) : Parcelable

    @Serializable
    @Parcelize
    data class LaunchFailureDetails(
        @SerializedName("time") var time: Int = 0,
        @SerializedName("altitude") var altitude: Int? = null,
        @SerializedName("reason") var reason: String = "",
    ) : Parcelable

    @Serializable
    @Parcelize
    data class Links(
        @SerializedName("mission_patch") var missionPatch: String? = null,
        @SerializedName("article_link") var articleLink: String? = null,
        @SerializedName("wikipedia") var wikipedia: String? = null,
        @SerializedName("video_link") var videoLink: String? = null,
    ) : Parcelable

}