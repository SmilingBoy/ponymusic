package me.wcy.music.net

import androidx.room.Room
import com.blankj.utilcode.util.LogUtils
import kotlinx.coroutines.runBlocking
import me.wcy.music.account.AccountApi
import me.wcy.music.account.bean.navidrome.PasswordLoginRequest
import me.wcy.music.account.service.UserServiceModule.Companion.userService
import me.wcy.music.net.NetUtils
import me.wcy.music.storage.db.MusicDatabase
import me.wcy.music.storage.db.entity.MusicServiceEntity
import okhttp3.Interceptor
import okhttp3.Response
import top.wangchenyan.common.CommonApp
import top.wangchenyan.common.model.CommonResult
import java.io.IOException

class TokenExpiredInterceptor(
) : Interceptor {

    // 懒加载数据库实例
    private val musicDatabase by lazy {
        Room.databaseBuilder(
            CommonApp.app,
            MusicDatabase::class.java,
            "music_db"
        ).build()
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalResponse = chain.proceed(originalRequest)

        // 检测401 token过期
        if (originalResponse.code == 401) {
            // 关闭原响应流，避免资源泄漏
            originalResponse.close()

            // 执行重新登录逻辑，并获取重试响应
            val retryResponse = handleTokenExpired(chain, originalResponse.message)
            // 登录重试成功则返回新响应，否则返回原401响应
            return retryResponse ?: originalResponse
        }

        return originalResponse
    }

    /**
     * token过期处理：重新登录 + 重试原请求
     */
    private fun handleTokenExpired(chain: Interceptor.Chain, errorMsg: String): Response? {
        LogUtils.d("TokenExpiredInterceptor: $errorMsg")

        // 从数据库获取默认音乐服务
        val defaultService = runBlocking {
            musicDatabase.musicServiceDao().getDefault()
        }

        // 本地无默认音乐服务或账号密码为空，直接返回null（无法自动登录）
        if (defaultService == null || defaultService.username.isBlank() || defaultService.password.isBlank()) {
            LogUtils.e("本地无默认音乐服务或账号密码，无法自动登录")
            return null
        }

        return try {
            // 同步执行协程登录（拦截器是同步线程，需用runBlocking）
            val loginResult = runBlocking {
                passwordLogin(defaultService.username, defaultService.password)
            }

            // 登录结果判断
            when {
                loginResult.isSuccess() -> {
                    LogUtils.d("自动登录成功，重试原请求")
                    // 登录成功后，重试原请求（新token已由userService保存）
                    retryOriginalRequest(chain)
                }

                else -> {
                    LogUtils.e("自动登录失败：${loginResult.msg}")
                    null
                }
            }
        } catch (e: Exception) {
            LogUtils.e("自动登录异常：${e.message}")
            null
        }
    }

    /**
     * 账号密码登录
     * @param account 账号
     * @param password 密码
     * @return 登录结果
     */
    private suspend fun passwordLogin(account: String, password: String): CommonResult<Unit> {
        val loginRes = kotlin.runCatching {
            AccountApi.get().passwordLogin(PasswordLoginRequest(account, password))
        }

        return if (loginRes.isSuccess) {
            val data = loginRes.getOrNull()

            if (data?.error.isNullOrBlank()) {
                if (data!= null) {
                    CommonApp.app.userService().navidromeLogin(data)
                    CommonResult.success(Unit)
                } else {
                    CommonResult.fail(-1, "登录失败")
                }
            } else {
                CommonResult.fail(-1, data.error ?: "登录失败")
            }

        } else {
            NetUtils.parseErrorResponse(loginRes.exceptionOrNull())
        }
    }

    /**
     * 重试原请求（使用新登录的token）
     */
    private fun retryOriginalRequest(chain: Interceptor.Chain): Response {
        val navidromeToken = CommonApp.app.userService().getNavidromeToken()

        val originalRequest = chain.request()
        // 重新构建请求，添加新的token header（根据你的实际header格式修改）
        val newRequest = originalRequest.newBuilder()
            .removeHeader("x-nd-authorization") // 移除旧token
            .addHeader("x-nd-authorization", "Bearer $navidromeToken") // 添加新token
            .build()

        // 重试请求并返回新响应
        return chain.proceed(newRequest)
    }
}