package me.wcy.music.service.likesong

import android.app.Activity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.wcy.music.account.AccountPreference
import me.wcy.music.account.service.UserService
import me.wcy.music.mine.MineApi
import top.wangchenyan.common.model.CommonResult
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by wangchenyan.top on 2024/3/21.
 */
@Singleton
class LikeSongProcessorImpl @Inject constructor(
    private val userService: UserService
) : LikeSongProcessor, CoroutineScope by MainScope() {
    private val likeSongSet = mutableSetOf<String>()

    override fun init() {
        launch {
            userService.profile.collectLatest {
                if (it != null) {
                    updateLikeSongList()
                } else {
                    likeSongSet.clear()
                }
            }
        }
    }

    override fun updateLikeSongList() {
        if (userService.isLogin().not()) return
        launch {
            val res = kotlin.runCatching {
                MineApi.get().getCollectSongList(userService.getUserId())
            }
            val data = res.getOrNull()
            if (data != null) {
                likeSongSet.clear()
                likeSongSet.addAll(data.map { it.id })
            }
        }
    }

    override fun isLiked(id: String): Boolean {
        if (userService.isLogin().not()) {
            return false
        }
        return likeSongSet.contains(id)
    }

    override suspend fun like(activity: Activity, id: String): CommonResult<Unit> {
        if (userService.isLogin().not()) {
            userService.checkLogin(activity)
            return CommonResult.fail()
        }

        val navidromeLogin = AccountPreference.navidromeLogin ?: return CommonResult.fail()
        val isLike = isLiked(id)
        if (!isLike) {
            val res = kotlin.runCatching {
                MineApi.get().addCollect(
                    username = navidromeLogin.username,
                    salt = navidromeLogin.subsonicSalt,
                    saltToken = navidromeLogin.subsonicToken,
                    id = id
                )
            }
            return if (res.isSuccess) {
                likeSongSet.add(id)
                updateLikeSongList()
                CommonResult.success(Unit)
            } else {
                CommonResult.fail()
            }
        } else {
            val res = kotlin.runCatching {
                MineApi.get().removeCollect(
                    username = navidromeLogin.username,
                    salt = navidromeLogin.subsonicSalt,
                    saltToken = navidromeLogin.subsonicToken,
                    id = id
                )
            }
            return if (res.isSuccess) {
                likeSongSet.remove(id)
                updateLikeSongList()
                CommonResult.success(Unit)
            } else {
                CommonResult.fail()
            }
        }
    }
}