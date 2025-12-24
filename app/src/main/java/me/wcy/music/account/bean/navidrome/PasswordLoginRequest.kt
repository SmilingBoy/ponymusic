package me.wcy.music.account.bean.navidrome

/**
 * 密码登录请求体
 */
data class PasswordLoginRequest(
    val username: String,
    val password: String
)