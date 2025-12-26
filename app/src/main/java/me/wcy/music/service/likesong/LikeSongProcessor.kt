package me.wcy.music.service.likesong

import android.app.Activity
import top.wangchenyan.common.model.CommonResult


/**
 * Created by wangchenyan.top on 2024/3/21.
 */
interface LikeSongProcessor {
    
    fun init()

    fun updateLikeSongList()

    fun isLiked(id: String): Boolean

    suspend fun like(activity: Activity, id: String): CommonResult<Unit>
}