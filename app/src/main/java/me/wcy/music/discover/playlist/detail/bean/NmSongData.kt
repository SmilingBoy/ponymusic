package me.wcy.music.discover.playlist.detail.bean


import com.google.gson.annotations.SerializedName

data class NmSongData(
    @SerializedName("album")
    var album: String = "", // 专辑名称
    @SerializedName("albumArtist")
    var albumArtist: String = "", // 专辑艺术家
    @SerializedName("albumArtistId")
    var albumArtistId: String = "", // 专辑艺术家ID
    @SerializedName("albumId")
    var albumId: String = "", // 专辑ID
    @SerializedName("artist")
    var artist: String = "", // 艺术家名称
    @SerializedName("artistId")
    var artistId: String = "", // 艺术家ID
    @SerializedName("bitRate")
    var bitRate: Int = 0, // 比特率
    @SerializedName("bookmarkPosition")
    var bookmarkPosition: Int = 0, // 书签位置
    @SerializedName("channels")
    var channels: Int = 0, // 声道数
    @SerializedName("compilation")
    var compilation: Boolean = false, // 是否为合辑
    @SerializedName("createdAt")
    var createdAt: String = "", // 创建时间
    @SerializedName("date")
    var date: String = "", // 日期
    @SerializedName("discNumber")
    var discNumber: Int = 0, // 碟片编号
    @SerializedName("duration")
    var duration: Double = 0.0, // 歌曲时长（秒）
    @SerializedName("genre")
    var genre: String = "", // 流派
    @SerializedName("genres")
    var genres: Any? = Any(), // 流派列表
    @SerializedName("hasCoverArt")
    var hasCoverArt: Boolean = false, // 是否有封面
    @SerializedName("id")
    var id: String = "", // 歌曲ID
    @SerializedName("libraryId")
    var libraryId: Int = 0, // 库ID
    @SerializedName("lyrics")
    var lyrics: String = "", // 歌词
    @SerializedName("orderAlbumArtistName")
    var orderAlbumArtistName: String = "", // 排序用的专辑艺术家名称
    @SerializedName("orderAlbumName")
    var orderAlbumName: String = "", // 排序用的专辑名称
    @SerializedName("orderArtistName")
    var orderArtistName: String = "", // 排序用的艺术家名称
    @SerializedName("orderTitle")
    var orderTitle: String = "", // 排序用的歌曲标题
    @SerializedName("originalYear")
    var originalYear: Int = 0, // 原始发行年份
    @SerializedName("path")
    var path: String = "", // 文件路径
    @SerializedName("playCount")
    var playCount: Long = 0, // 播放次数
    @SerializedName("playDate")
    var playDate: Any? = Any(), // 播放日期
    @SerializedName("rating")
    var rating: Int = 0, // 评分
    @SerializedName("releaseYear")
    var releaseYear: Int = 0, // 发行年份
    @SerializedName("rgAlbumGain")
    var rgAlbumGain: Int = 0, // 专辑增益
    @SerializedName("rgAlbumPeak")
    var rgAlbumPeak: Int = 0, // 专辑峰值
    @SerializedName("rgTrackGain")
    var rgTrackGain: Int = 0, // 音轨增益
    @SerializedName("rgTrackPeak")
    var rgTrackPeak: Int = 0, // 音轨峰值
    @SerializedName("sampleRate")
    var sampleRate: Int = 0, // 采样率
    @SerializedName("size")
    var size: Int = 0, // 文件大小
    @SerializedName("starred")
    var starred: Boolean = false, // 是否收藏
    @SerializedName("starredAt")
    var starredAt: Any? = Any(), // 收藏时间
    @SerializedName("suffix")
    var suffix: String = "", // 文件后缀
    @SerializedName("title")
    var title: String = "", // 歌曲标题
    @SerializedName("trackNumber")
    var trackNumber: Int = 0, // 音轨编号
    @SerializedName("updatedAt")
    var updatedAt: String = "", // 更新时间
    @SerializedName("year")
    var year: Int = 0 // 年份
)