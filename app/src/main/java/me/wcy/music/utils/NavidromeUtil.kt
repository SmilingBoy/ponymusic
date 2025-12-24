package me.wcy.music.utils

import com.blankj.utilcode.util.LogUtils
import me.wcy.music.account.AccountPreference
import me.wcy.music.storage.preference.ConfigPreferences

object NavidromeUtil {


    /**
     * 获取封面
     * http://127.0.0.1:9003/rest/getCoverArt?u=admin&t=ff862c44347581944309e24c011c835c&s=b56c9f&f=json&v=1.8.0&c=NavidromeUI&id=al-a0b5b467707b90008270714c3eb7c7be&_=2025-12-24T15%3A55%3A08%2B08%3A00&size=300&square=true
     *
     * @param songId 歌曲id
     */
    fun getCover(songId: String): String? {

        val s = AccountPreference.navidromeLogin?.let {
            val url = ConfigPreferences.apiDomain + "rest/getCoverArt"
            return url + "?u=" + it.username + "&t=" + it.subsonicToken + "&s=" + it.subsonicSalt + "&f=json&v=1.8.0&c=NavidromeUI&id=" + songId + "&_=" + System.currentTimeMillis() + "&size=300&square=true"
        }

        LogUtils.d("getCover: $s")
        return s
    }

}