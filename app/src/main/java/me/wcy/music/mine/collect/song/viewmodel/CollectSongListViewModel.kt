package me.wcy.music.mine.collect.song.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.wcy.music.discover.playlist.detail.bean.NmSongData
import me.wcy.music.mine.MineApi
import me.wcy.music.service.PlayerController
import me.wcy.music.utils.toNmMediaItem
import top.wangchenyan.common.ext.toUnMutable
import javax.inject.Inject

/**
 * 收藏歌曲列表ViewModel
 */
@HiltViewModel
class CollectSongListViewModel @Inject constructor(
    private val playerController: PlayerController
) : ViewModel() {
    private val _collectSongs = MutableStateFlow<List<NmSongData>>(emptyList())
    val collectSongs = _collectSongs.toUnMutable()

     fun loadCollectSongs() {
         viewModelScope.launch {
             val songs = MineApi.Companion.get().getCollectSongList()
             val songDataList = songs
             _collectSongs.value = songDataList
             LogUtils.d("收藏歌曲列表：${songDataList.size}")
         }
     }

    fun playSong(song: NmSongData) {
        playerController.addAndPlay(song.toNmMediaItem())
    }

}