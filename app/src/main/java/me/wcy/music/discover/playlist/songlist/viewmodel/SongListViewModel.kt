package me.wcy.music.discover.playlist.songlist.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.wcy.music.discover.DiscoverApi
import me.wcy.music.discover.playlist.detail.bean.NmSongData
import me.wcy.music.service.PlayerController
import me.wcy.music.utils.toNmMediaItem
import top.wangchenyan.common.model.CommonResult
import javax.inject.Inject

/**
 * 歌曲列表ViewModel
 */
@HiltViewModel
class SongListViewModel @Inject constructor(
    private val playerController: PlayerController
) : ViewModel() {
    private val _songs = MutableStateFlow<List<NmSongData>>(emptyList())
    val songs: StateFlow<List<NmSongData>> = _songs.asStateFlow()

    suspend fun loadSongs(page: Int = 1): CommonResult<List<NmSongData>> {
        return try {
            val res = kotlin.runCatching {
                DiscoverApi.get()
                    .getSongList(
                        sort = "title",
                        order = "ASC",
                        start = 30 * (page - 1),
                        end = 30 * page
                    )
            }
            val songDataList = res.getOrThrow()

            _songs.value = songDataList
            CommonResult.success(songDataList)
        } catch (e: Exception) {
            CommonResult.fail(msg = e.message)
        }
    }

    fun playSong(song: NmSongData) {
        playerController.addAndPlay(song.toNmMediaItem())
    }
}