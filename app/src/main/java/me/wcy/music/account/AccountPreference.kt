package me.wcy.music.account

import me.wcy.music.account.bean.ProfileData
import me.wcy.music.account.bean.navidrome.LoginResultBean
import me.wcy.music.account.bean.navidrome.UserProfileBean
import me.wcy.music.consts.PreferenceName
import top.wangchenyan.common.CommonApp
import top.wangchenyan.common.storage.IPreferencesFile
import top.wangchenyan.common.storage.PreferencesFile

/**
 * Created by wangchenyan.top on 2023/8/28.
 */
object AccountPreference :
    IPreferencesFile by PreferencesFile(CommonApp.app, PreferenceName.ACCOUNT, false) {
    var cookie by IPreferencesFile.StringProperty("cookie", "")
    var profile by IPreferencesFile.ObjectProperty("profile", ProfileData::class.java)

    var navidromeProfile by IPreferencesFile.ObjectProperty(
        "navidromeProfile",
        UserProfileBean::class.java
    )

    // Navidrome登录信息
    var navidromeLogin by IPreferencesFile.ObjectProperty(
        "navidromeLogin",
        LoginResultBean::class.java
    )
}