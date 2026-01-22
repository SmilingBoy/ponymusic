package me.wcy.music.discover

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.wcy.music.common.bean.LrcDataWrap
import me.wcy.music.common.bean.SongData
import me.wcy.music.common.bean.SongUrlData
import me.wcy.music.discover.artist.bean.NmArtistData
import me.wcy.music.discover.banner.BannerListData
import me.wcy.music.discover.playlist.detail.bean.NmAlbumData
import me.wcy.music.discover.playlist.detail.bean.NmSongData
import me.wcy.music.discover.playlist.detail.bean.PlaylistDetailData
import me.wcy.music.discover.playlist.detail.bean.SongListData
import me.wcy.music.discover.playlist.square.bean.PlaylistListData
import me.wcy.music.discover.playlist.square.bean.PlaylistTagListData
import me.wcy.music.discover.recommend.song.bean.RecommendSongListData
import me.wcy.music.net.HttpClient
import me.wcy.music.storage.preference.ConfigPreferences
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import top.wangchenyan.common.net.NetResult
import top.wangchenyan.common.net.gson.GsonConverterFactory
import top.wangchenyan.common.utils.GsonUtils

/**
 * Created by wangchenyan.top on 2023/9/6.
 */
interface DiscoverApi {

    @POST("recommend/songs")
    suspend fun getRecommendSongs(): NetResult<RecommendSongListData>

    @POST("recommend/resource")
    suspend fun getRecommendPlaylists(): PlaylistListData

    @POST("song/url/v1")
    suspend fun getSongUrl(
        @Query("id") id: Long,
        @Query("level") level: String,
    ): NetResult<List<SongUrlData>>

    @POST("lyric")
    suspend fun getLrc(
        @Query("id") id: Long,
    ): LrcDataWrap

    @POST("playlist/detail")
    suspend fun getPlaylistDetail(
        @Query("id") id: Long,
    ): PlaylistDetailData

    @POST("playlist/track/all")
    suspend fun getPlaylistSongList(
        @Query("id") id: Long,
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null,
        @Query("timestamp") timestamp: Long? = null
    ): SongListData

    @POST("playlist/hot")
    suspend fun getPlaylistTagList(): PlaylistTagListData

    @POST("top/playlist")
    suspend fun getPlaylistList(
        @Query("cat") cat: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
    ): PlaylistListData

    @POST("toplist")
    suspend fun getRankingList(): PlaylistListData

    @GET("banner?type=2")
    suspend fun getBannerList(): BannerListData


    /**
     * ==========================Navidrome API==========================
     */


    /**
     * 获取歌单歌曲列表
     * http://127.0.0.1:9003/api/playlist/90e24be5-bf4d-4d54-a7b8-bd6e6cb5a1b5/tracks?_end=100&_order=ASC&_sort=id&_start=0&playlist_id=90e24be5-bf4d-4d54-a7b8-bd6e6cb5a1b5
     */
    @GET("api/playlist/{pid}/tracks")
    suspend fun getPlaylistSongList(
        @Path("pid") pid: String,
        @Query("_end") end: Int = 100,
        @Query("_order") order: String = "ASC",
        @Query("_sort") sort: String = "id",
        @Query("_start") start: Int = 0,
        @Query("playlist_id") playlistId: String = "",
    ): List<NmSongData>

    /**
     * 歌曲详情
     * http://127.0.0.1:9003/api/song/18551f5f78e968d5c730408dd23d13a8
     */
    @GET("api/song/{id}")
    suspend fun getSongDetail(
        @Path("id") id: String
    ): NmSongData

    /**
     * 专辑歌曲详情
     * http://127.0.0.1:9003/api/song?_end=-1&_order=ASC&_sort=trackNumber&_start=0&album_id=885b3c2609933c220a8ce87d95d89c63
     */
    @GET("api/song")
    suspend fun getSongList(
        @Query("_end") end: Int = -1,
        @Query("_order") order: String = "ASC",
        @Query("_sort") sort: String = "trackNumber",
        @Query("_start") start: Int = 0,
        @Query("seed") seed: String = "",
        @Query("album_id") albumId: String = "",
        @Query("title") title: String = "",
    ): List<NmSongData>

    /**
     * 专辑列表
     */
    @GET("api/album")
    suspend fun getAlbumList(
        @Query("_end") end: Int = 20,
        @Query("_order") order: String = "ASC",
        @Query("_sort") sort: String = "random",
        @Query("_start") start: Int = 0,
        @Query("seed") seed: String = "",
        @Query("name") name: String = "",
    ): List<NmAlbumData>

    /**
     * http://127.0.0.1:9003/rest/scrobble?u=admin&t=ff862c44347581944309e24c011c835c&s=b56c9f&f=json&v=1.8.0&c=NavidromeUI&id=d55b3bc6b9c04221681f1ed5ce38e36e&submission=false
     * http://127.0.0.1:9003/rest/scrobble?u=admin&t=ff862c44347581944309e24c011c835c&s=b56c9f&f=json&v=1.8.0&c=NavidromeUI&id=acb4cd8dfd26fb817e1eb22ae48ee761&time=1766560589952&submission=true
     *
     * 提交歌曲播放
     */
    @GET("rest/scrobble")
    suspend fun scrobble(
        @Query("f") format: String = "json",
        @Query("v") version: String = "1.8.0",
        @Query("c") client: String = "NavidromeUI",
        @Query("u") username: String,
        @Query("s") salt: String,
        @Query("t") saltToken: String,
        @Query("id") id: String,
        @Query("submission") submission: Boolean,
        @Query("time") time: Long? = null
    ): Any

    //http://127.0.0.1:9003/api/artist?_end=30&_order=ASC&_sort=name&_start=15

    /**
     * 歌手列表
     */
    @GET("api/artist")
    suspend fun getArtistList(
        @Query("_start") start: Int = 0,
        @Query("_end") end: Int = 30,
        @Query("_order") order: String = "ASC",
        @Query("_sort") sort: String = "name",
        @Query("name") name: String = "",
    ): List<NmArtistData>

    //http://127.0.0.1:9003/api/album?_end=18&_order=DESC&_sort=play_date&_start=0&recently_played=true&seed=0.3406203128658801-12

    companion object {
        private const val SONG_LIST_LIMIT = 800

        private val api: DiscoverApi by lazy {
            val retrofit = Retrofit.Builder()
                .baseUrl(ConfigPreferences.apiDomain)
                .addConverterFactory(GsonConverterFactory.create(GsonUtils.gson, true))
                .client(HttpClient.okHttpClient)
                .build()
            retrofit.create(DiscoverApi::class.java)
        }

        fun get(): DiscoverApi = api

        suspend fun getFullPlaylistSongList(id: Long, timestamp: Long? = null): SongListData {
            return withContext(Dispatchers.IO) {
                var offset = 0
                val list = mutableListOf<SongData>()
                while (true) {
                    val songList = get().getPlaylistSongList(
                        id,
                        limit = SONG_LIST_LIMIT,
                        offset = offset,
                        timestamp = timestamp
                    )
                    if (songList.code != 200) {
                        throw Exception("code = ${songList.code}")
                    }
                    if (songList.songs.isEmpty()) {
                        break
                    }
                    list.addAll(songList.songs)
                    offset = list.size
                }
                return@withContext SongListData(200, list)
            }
        }
    }
}