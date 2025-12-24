package me.wcy.music.net.datasource

import android.net.Uri
import kotlinx.coroutines.runBlocking
import me.wcy.music.account.AccountPreference
import me.wcy.music.storage.preference.ConfigPreferences

/**
 * Created by wangchenyan.top on 2024/3/26.
 */
object OnlineMusicUriFetcher {

    fun fetchPlayUrl(uri: Uri): String {
        val songId = uri.getQueryParameter("id") ?: return uri.toString()
        return runBlocking {
            return@runBlocking AccountPreference.navidromeLogin?.let {
                ConfigPreferences.apiDomain + "rest/stream?u=" +
                        it.username + "&t=" +
                        it.subsonicToken + "&s=" +
                        it.subsonicSalt + "&f=json&v=1.8.0&c=NavidromeUI&id=" +
                        songId + "&_=" + System.currentTimeMillis()
            } ?: ""
        }
    }
}