package me.wcy.music.account.service

import android.app.Activity
import kotlinx.coroutines.flow.StateFlow
import top.wangchenyan.common.model.CommonResult
import me.wcy.music.account.bean.ProfileData
import me.wcy.music.account.bean.navidrome.LoginResultBean
import me.wcy.music.account.bean.navidrome.UserProfileBean

/**
 * Created by wangchenyan.top on 2023/9/18.
 */
interface UserService {
    val profile: StateFlow<ProfileData?>
    val navidromeProfile: StateFlow<UserProfileBean?>

    fun getCookie(): String

    fun isLogin(): Boolean

    fun getUserId(): String

    suspend fun login(cookie: String): CommonResult<ProfileData>

    suspend fun logout()


    suspend fun navidromeLogin(loginResultBean: LoginResultBean): List<UserProfileBean>

    fun getNavidromeToken(): String

    fun checkLogin(
        activity: Activity,
        showDialog: Boolean = true,
        onCancel: (() -> Unit)? = null,
        onLogin: (() -> Unit)? = null
    )
}