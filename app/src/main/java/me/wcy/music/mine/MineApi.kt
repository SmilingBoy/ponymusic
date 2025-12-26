package me.wcy.music.mine

import me.wcy.music.discover.playlist.detail.bean.NmSongData
import me.wcy.music.discover.playlist.square.bean.PlaylistListData
import me.wcy.music.mine.bean.NmPlaylistBean
import me.wcy.music.mine.bean.NmRestData
import me.wcy.music.mine.collect.song.bean.CollectSongResult
import me.wcy.music.net.HttpClient
import me.wcy.music.service.likesong.bean.LikeSongListData
import me.wcy.music.storage.preference.ConfigPreferences
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import top.wangchenyan.common.net.NetResult
import top.wangchenyan.common.net.gson.GsonConverterFactory
import top.wangchenyan.common.utils.GsonUtils
import top.wangchenyan.common.utils.ServerTime

/**
 * Created by wangchenyan.top on 2023/9/26.
 */
interface MineApi {

    @POST("user/playlist")
    suspend fun getUserPlaylist(
        @Query("uid") uid: Long,
        @Query("limit") limit: Int = 1000,
        @Query("timestamp") timestamp: Long = ServerTime.currentTimeMillis()
    ): PlaylistListData

    /**
     * 收藏/取消收藏歌单
     * @param id 歌单 id
     * @param t 类型,1:收藏,2:取消收藏
     */
    @POST("playlist/subscribe")
    suspend fun collectPlaylist(
        @Query("id") id: Long,
        @Query("t") t: Int,
        @Query("timestamp") timestamp: Long = ServerTime.currentTimeMillis()
    ): NetResult<Any>

    /**
     * 对歌单添加歌曲
     * @param op 从歌单增加单曲为 add, 删除为 del
     * @param pid 歌单 id
     * @param tracks 歌曲 id,可多个,用逗号隔开
     */
    @POST("playlist/tracks")
    suspend fun collectSong(
        @Query("pid") pid: Long,
        @Query("tracks") tracks: String,
        @Query("op") op: String = "add",
        @Query("timestamp") timestamp: Long = ServerTime.currentTimeMillis()
    ): CollectSongResult

    /**
     * 喜欢音乐
     * @param id 歌曲 id
     * @param like 默认为 true 即喜欢 , 若传 false, 则取消喜欢
     */
    @POST("like")
    suspend fun likeSong(
        @Query("id") id: Long,
        @Query("like") like: Boolean = true,
        @Query("timestamp") timestamp: Long = ServerTime.currentTimeMillis()
    ): NetResult<Any>

    /**
     * 喜欢音乐列表
     */
    @POST("likelist")
    suspend fun getMyLikeSongList(
        @Query("uid") uid: Long,
        @Query("timestamp") timestamp: Long = ServerTime.currentTimeMillis()
    ): LikeSongListData

    /**
     * ==============================Navidrome API==============================
     */

    /**
     * 获取用户歌单
     *
     * _end=15&_order=ASC&_sort=id&_start=0
     */
    @GET("api/playlist")
    suspend fun getPlaylist(
        @Query("_end") _end: Int = 15,
        @Query("_order") _order: String = "ASC",
        @Query("_sort") _sort: String = "id",
        @Query("_start") _start: Int = 0
    ): List<NmPlaylistBean>

    //http://127.0.0.1:9003/api/album?_end=12&_order=DESC&_sort=starred_at&_start=0&seed=0.20247334231290426-0&starred=true
    /**
     * 收藏列表
     */
    @GET("api/song")
    suspend fun getCollectSongList(
//        @Query("_end") end: Int = -1,
        @Query("_order") order: String = "DESC",
        @Query("_sort") sort: String = "starred_at",
        @Query("_start") start: Int = 0,
        @Query("seed") seed: String = "",
        @Query("album_id") albumId: String = "",
        @Query("starred") starred: Boolean = true,
    ): List<NmSongData>


    /**
     * 添加收藏
     */
    @GET("rest/star")
    suspend fun addCollect(
        @Query("f") format: String = "json",
        @Query("v") version: String = "1.8.0",
        @Query("c") client: String = "NavidromeUI",
        @Query("u") username: String,
        @Query("s") salt: String,
        @Query("t") saltToken: String,
        @Query("id") id: String,
    ): NetResult<NmRestData>

    /**
     * 取消收藏
     */
    @GET("rest/unstar")
    suspend fun removeCollect(
        @Query("f") format: String = "json",
        @Query("v") version: String = "1.8.0",
        @Query("c") client: String = "NavidromeUI",
        @Query("u") username: String,
        @Query("s") salt: String,
        @Query("t") saltToken: String,
        @Query("id") id: String,
    ): NetResult<NmRestData>


    companion object {
        private val api: MineApi by lazy {
            val retrofit = Retrofit.Builder()
                .baseUrl(ConfigPreferences.apiDomain)
                .addConverterFactory(GsonConverterFactory.create(GsonUtils.gson, true))
                .client(HttpClient.okHttpClient)
                .build()
            retrofit.create(MineApi::class.java)
        }

        fun get(): MineApi = api
    }
}