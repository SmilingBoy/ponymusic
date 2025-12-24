package me.wcy.music.mine.bean


import com.google.gson.annotations.SerializedName

data class NmPlaylistBean(
    @SerializedName("comment")
    var comment: String = "",
    @SerializedName("createdAt")
    var createdAt: String = "",
    @SerializedName("duration")
    var duration: Int? = 0,
    @SerializedName("evaluatedAt")
    var evaluatedAt: Any? = Any(),
    @SerializedName("id")
    var id: String = "",
    @SerializedName("name")
    var name: String = "",
    @SerializedName("ownerId")
    var ownerId: String = "",
    @SerializedName("ownerName")
    var ownerName: String = "",
    @SerializedName("path")
    var path: String = "",
    @SerializedName("public")
    var `public`: Boolean = false,
    @SerializedName("rules")
    var rules: Any? = Any(),
    @SerializedName("size")
    var size: Int = 0,
    @SerializedName("songCount")
    var songCount: Int = 0,
    @SerializedName("sync")
    var sync: Boolean = false,
    @SerializedName("updatedAt")
    var updatedAt: String = ""
)