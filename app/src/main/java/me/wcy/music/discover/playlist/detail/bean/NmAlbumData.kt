package me.wcy.music.discover.playlist.detail.bean


import com.google.gson.annotations.SerializedName

data class NmAlbumData(
    @SerializedName("albumArtist")
    var albumArtist: String = "",
    @SerializedName("albumArtistId")
    var albumArtistId: String = "",
    @SerializedName("allArtistIds")
    var allArtistIds: String = "",
    @SerializedName("artist")
    var artist: String = "",
    @SerializedName("artistId")
    var artistId: String = "",
    @SerializedName("compilation")
    var compilation: Boolean = false,
    @SerializedName("createdAt")
    var createdAt: String = "",
    @SerializedName("duration")
    var duration: Double = 0.0,
    @SerializedName("embedArtPath")
    var embedArtPath: String = "",
    @SerializedName("externalInfoUpdatedAt")
    var externalInfoUpdatedAt: Any? = Any(),
    @SerializedName("genre")
    var genre: String = "",
    @SerializedName("genres")
    var genres: Any? = Any(),
    @SerializedName("id")
    var id: String = "",
    @SerializedName("libraryId")
    var libraryId: Int = 0,
    @SerializedName("maxOriginalYear")
    var maxOriginalYear: Int = 0,
    @SerializedName("maxYear")
    var maxYear: Int = 0,
    @SerializedName("minOriginalYear")
    var minOriginalYear: Int = 0,
    @SerializedName("minYear")
    var minYear: Int = 0,
    @SerializedName("name")
    var name: String = "",
    @SerializedName("orderAlbumArtistName")
    var orderAlbumArtistName: String = "",
    @SerializedName("orderAlbumName")
    var orderAlbumName: String = "",
    @SerializedName("paths")
    var paths: String = "",
    @SerializedName("playCount")
    var playCount: Int = 0,
    @SerializedName("playDate")
    var playDate: String = "",
    @SerializedName("rating")
    var rating: Int = 0,
    @SerializedName("releases")
    var releases: Int = 0,
    @SerializedName("size")
    var size: Int = 0,
    @SerializedName("songCount")
    var songCount: Int = 0,
    @SerializedName("starred")
    var starred: Boolean = false,
    @SerializedName("starredAt")
    var starredAt: Any? = Any(),
    @SerializedName("updatedAt")
    var updatedAt: String = ""
)

/*
{
        "playCount": 3,
        "playDate": "2025-12-24T11:02:09.907+08:00",
        "rating": 0,
        "starred": false,
        "starredAt": null,
        "id": "885b3c2609933c220a8ce87d95d89c63",
        "libraryId": 1,
        "name": "100%契合度",
        "embedArtPath": "/vol1/1000/Music/202512221/02网络流行歌曲/0569.%契合度 宋厦.mp3",
        "artistId": "4ecf6cbb018f5a3e8745a4510156398d",
        "artist": "宋厦",
        "albumArtistId": "4ecf6cbb018f5a3e8745a4510156398d",
        "albumArtist": "宋厦",
        "allArtistIds": "4ecf6cbb018f5a3e8745a4510156398d",
        "maxYear": 0,
        "minYear": 0,
        "maxOriginalYear": 0,
        "minOriginalYear": 0,
        "releases": 1,
        "compilation": false,
        "songCount": 1,
        "duration": 185.63,
        "size": 7439985,
        "genre": "",
        "genres": null,
        "orderAlbumName": "100%契合度",
        "orderAlbumArtistName": "宋厦",
        "paths": "/vol1/1000/Music/202512221/02网络流行歌曲",
        "externalInfoUpdatedAt": null,
        "createdAt": "2025-12-22T15:10:59.975897286+08:00",
        "updatedAt": "2024-03-14T17:28:34+08:00"
    }
 */