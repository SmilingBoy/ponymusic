package me.wcy.music.discover.artist.bean

import com.google.gson.annotations.SerializedName

data class NmArtistData(
    @SerializedName("albumCount")
    var albumCount: Int = 0,
    @SerializedName("externalInfoUpdatedAt")
    var externalInfoUpdatedAt: String = "",
    @SerializedName("genres")
    var genres: Any = Any(),
    @SerializedName("id")
    var id: String = "",
    @SerializedName("name")
    var name: String = "",
    @SerializedName("orderArtistName")
    var orderArtistName: String = "",
    @SerializedName("playCount")
    var playCount: Int = 0,
    @SerializedName("playDate")
    var playDate: Any = Any(),
    @SerializedName("rating")
    var rating: Int = 0,
    @SerializedName("size")
    var size: Int = 0,
    @SerializedName("songCount")
    var songCount: Int = 0,
    @SerializedName("starred")
    var starred: Boolean = false,
    @SerializedName("starredAt")
    var starredAt: Any = Any()
)