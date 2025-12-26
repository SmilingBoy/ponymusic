package me.wcy.music.mine.bean
import com.google.gson.annotations.SerializedName

data class NmRestData(
    @SerializedName("subsonic-response")
    var subsonicResponse: SubsonicResponse = SubsonicResponse()
)

data class SubsonicResponse(
    @SerializedName("openSubsonic")
    var openSubsonic: Boolean = false,
    @SerializedName("serverVersion")
    var serverVersion: String = "",
    @SerializedName("status")
    var status: String = "",
    @SerializedName("type")
    var type: String = "",
    @SerializedName("version")
    var version: String = ""
)


