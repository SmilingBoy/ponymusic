package me.wcy.music.discover.playlist.detail.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import me.wcy.music.common.bean.PlaylistData
import me.wcy.music.common.bean.SongData
import me.wcy.music.discover.DiscoverApi
import me.wcy.music.discover.playlist.detail.bean.NmSongData
import me.wcy.music.mine.MineApi
import me.wcy.music.service.likesong.LikeSongProcessor
import top.wangchenyan.common.ext.toUnMutable
import top.wangchenyan.common.model.CommonResult
import top.wangchenyan.common.net.apiCall
import javax.inject.Inject

/**
 * Created by wangchenyan.top on 2023/9/22.
 */
@HiltViewModel
class PlaylistViewModel @Inject constructor() : ViewModel() {
    @Inject
    lateinit var likeSongProcessor: LikeSongProcessor

    private val _playlistData = MutableStateFlow<PlaylistData?>(null)
    val playlistData = _playlistData.toUnMutable()

    private val _songList = MutableStateFlow<List<NmSongData>>(emptyList())
    val songList = _songList.toUnMutable()

    private var playlistId = ""
    private var realtimeData = false
    private var isLike = false

    fun init(playlistId: String, realtimeData: Boolean, isLike: Boolean) {
        this.playlistId = playlistId
        this.realtimeData = realtimeData
        this.isLike = isLike
    }

    suspend fun loadData(): CommonResult<Unit> {
        val detailRes = kotlin.runCatching {
            DiscoverApi.get().getPlaylistSongList(playlistId = playlistId, pid = playlistId)
        }
        val songListRes = kotlin.runCatching {
            DiscoverApi.get().getPlaylistSongList(playlistId = playlistId, pid = playlistId)
        }
        return if (detailRes.isSuccess.not()) {
            CommonResult.fail(msg = detailRes.exceptionOrNull()?.message)
        } else if (songListRes.isSuccess.not()) {
            CommonResult.fail(msg = songListRes.exceptionOrNull()?.message)
        } else {
//            _playlistData.value = detailRes.getOrThrow()
            _songList.value = songListRes.getOrThrow()
            CommonResult.success(Unit)
        }
    }

    suspend fun collect(): CommonResult<Unit> {
        val playlistData = _playlistData.value ?: return CommonResult.fail()
        if (playlistData.subscribed) {
            val res = apiCall {
                MineApi.get().collectPlaylist(playlistData.id, 2)
            }
            return if (res.isSuccess()) {
                _playlistData.value = playlistData.copy(subscribed = false)
                CommonResult.success(Unit)
            } else {
                CommonResult.fail(res.code, res.msg)
            }
        } else {
            val res = apiCall {
                MineApi.get().collectPlaylist(playlistData.id, 1)
            }
            return if (res.isSuccess()) {
                _playlistData.value = playlistData.copy(subscribed = true)
                CommonResult.success(Unit)
            } else {
                CommonResult.fail(res.code, res.msg)
            }
        }
    }

    fun removeSong(songData: NmSongData) {
        val songList = _songList.value.toMutableList()
        songList.remove(songData)
        _songList.value = songList
        if (isLike) {
            likeSongProcessor.updateLikeSongList()
        }
    }
}