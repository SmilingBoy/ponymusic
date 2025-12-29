package me.wcy.music.search

import top.wangchenyan.common.net.NetResult
import top.wangchenyan.common.net.gson.GsonConverterFactory
import top.wangchenyan.common.utils.GsonUtils
import me.wcy.music.net.HttpClient
import me.wcy.music.search.bean.SearchResultData
import me.wcy.music.storage.preference.ConfigPreferences
import retrofit2.Retrofit
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by wangchenyan.top on 2023/9/20.
 */
interface SearchApi {

    /**
     * 搜索歌曲
     * @param type 搜索类型；默认为 1 即单曲 , 取值意义 :
     * - 1: 单曲,
     * - 10: 专辑,
     * - 100: 歌手,
     * - 1000: 歌单,
     * - 1002: 用户,
     * - 1004: MV,
     * - 1006: 歌词,
     * - 1009: 电台,
     * - 1014: 视频,
     * - 1018:综合,
     * - 2000:声音(搜索声音返回字段格式会不一样)
     */
    @POST("cloudsearch")
    suspend fun search(
        @Query("type") type: Int,
        @Query("keywords") keywords: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
    ): NetResult<SearchResultData>


    /**
     * ==============================NativeApi==============================
     */

    //http://127.0.0.1:9003/api/song?_end=15&_order=ASC&_sort=title&_start=0&title=%E7%88%B1%E6%83%85
    //http://127.0.0.1:9003/api/artist?_end=15&_order=ASC&_sort=name&_start=0&name=%E6%99%BA%E6%85%A7
    //http://127.0.0.1:9003/api/album?_end=18&_order=ASC&_sort=name&_start=0&name=%E6%B2%A1%E6%9C%89&seed=0.3406203128658801-9

    companion object {
        private val api: SearchApi by lazy {
            val retrofit = Retrofit.Builder()
                .baseUrl(ConfigPreferences.apiDomain)
                .addConverterFactory(GsonConverterFactory.create(GsonUtils.gson, true))
                .client(HttpClient.okHttpClient)
                .build()
            retrofit.create(SearchApi::class.java)
        }

        fun get(): SearchApi = api
    }
}