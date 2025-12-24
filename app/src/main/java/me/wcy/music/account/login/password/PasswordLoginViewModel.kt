package me.wcy.music.account.login.password

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import me.wcy.music.account.AccountApi
import me.wcy.music.account.bean.navidrome.PasswordLoginRequest
import me.wcy.music.account.service.UserService
import me.wcy.music.net.NetUtils
import top.wangchenyan.common.model.CommonResult
import javax.inject.Inject

/**
 * 账号密码登录ViewModel
 */
@HiltViewModel
class PasswordLoginViewModel @Inject constructor(
    private val userService: UserService
) : ViewModel() {

    /**
     * 账号密码登录
     * @param account 账号
     * @param password 密码
     * @return 登录结果
     */
    suspend fun passwordLogin(account: String, password: String): CommonResult<Unit> {
        val loginRes = kotlin.runCatching {
            AccountApi.get().passwordLogin(PasswordLoginRequest(account, password))
        }

        return if (loginRes.isSuccess) {
            val data = loginRes.getOrNull()

            if (data?.error.isNullOrBlank()) {
                if (data!= null) {
                    userService.navidromeLogin(data)
                    CommonResult.success(Unit)
                }else{
                    CommonResult.fail(-1, "登录失败")
                }
            } else {
                CommonResult.fail(-1, data.error ?: "登录失败")
            }

        } else {
            NetUtils.parseErrorResponse(loginRes.exceptionOrNull())
        }
    }
}