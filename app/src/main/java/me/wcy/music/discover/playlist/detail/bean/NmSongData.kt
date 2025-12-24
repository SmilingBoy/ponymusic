package me.wcy.music.discover.playlist.detail.bean


import com.google.gson.annotations.SerializedName

data class NmSongData(
    @SerializedName("album")
    var album: String = "",
    @SerializedName("albumArtist")
    var albumArtist: String = "",
    @SerializedName("albumArtistId")
    var albumArtistId: String = "",
    @SerializedName("albumId")
    var albumId: String = "",
    @SerializedName("artist")
    var artist: String = "",
    @SerializedName("artistId")
    var artistId: String = "",
    @SerializedName("bitRate")
    var bitRate: Int = 0,
    @SerializedName("bookmarkPosition")
    var bookmarkPosition: Int = 0,
    @SerializedName("channels")
    var channels: Int = 0,
    @SerializedName("compilation")
    var compilation: Boolean = false,
    @SerializedName("createdAt")
    var createdAt: String = "",
    @SerializedName("discNumber")
    var discNumber: Int = 0,
    @SerializedName("duration")
    var duration: Double = 0.0,
    @SerializedName("genre")
    var genre: String = "",
    @SerializedName("genres")
    var genres: Any? = Any(),
    @SerializedName("hasCoverArt")
    var hasCoverArt: Boolean = false,
    @SerializedName("id")
    var id: String = "",
    @SerializedName("libraryId")
    var libraryId: Int = 0,
    @SerializedName("lyrics")
    var lyrics: String = "",
    @SerializedName("mediaFileId")
    var mediaFileId: String = "",
    @SerializedName("orderAlbumArtistName")
    var orderAlbumArtistName: String = "",
    @SerializedName("orderAlbumName")
    var orderAlbumName: String = "",
    @SerializedName("orderArtistName")
    var orderArtistName: String = "",
    @SerializedName("orderTitle")
    var orderTitle: String = "",
    @SerializedName("originalYear")
    var originalYear: Int = 0,
    @SerializedName("path")
    var path: String = "",
    @SerializedName("playCount")
    var playCount: Int = 0,
    @SerializedName("playDate")
    var playDate: Any? = Any(),
    @SerializedName("playlistId")
    var playlistId: String = "",
    @SerializedName("rating")
    var rating: Int = 0,
    @SerializedName("releaseYear")
    var releaseYear: Int = 0,
    @SerializedName("rgAlbumGain")
    var rgAlbumGain: Int = 0,
    @SerializedName("rgAlbumPeak")
    var rgAlbumPeak: Int = 0,
    @SerializedName("rgTrackGain")
    var rgTrackGain: Int = 0,
    @SerializedName("rgTrackPeak")
    var rgTrackPeak: Int = 0,
    @SerializedName("sampleRate")
    var sampleRate: Int = 0,
    @SerializedName("size")
    var size: Int = 0,
    @SerializedName("starred")
    var starred: Boolean = false,
    @SerializedName("starredAt")
    var starredAt: Any? = Any(),
    @SerializedName("suffix")
    var suffix: String = "",
    @SerializedName("title")
    var title: String = "",
    @SerializedName("trackNumber")
    var trackNumber: Int = 0,
    @SerializedName("updatedAt")
    var updatedAt: String = "",
    @SerializedName("year")
    var year: Int = 0
)