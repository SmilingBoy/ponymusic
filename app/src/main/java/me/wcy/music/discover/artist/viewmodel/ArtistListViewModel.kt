package me.wcy.music.discover.artist.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.wcy.music.discover.DiscoverApi
import me.wcy.music.discover.artist.bean.NmArtistData
import top.wangchenyan.common.model.CommonResult
import javax.inject.Inject

/**
 * 歌手列表ViewModel
 */
@HiltViewModel
class ArtistListViewModel @Inject constructor() : ViewModel() {
    private val _artists = MutableStateFlow<List<NmArtistData>>(emptyList())
    val artists: StateFlow<List<NmArtistData>> = _artists.asStateFlow()

    suspend fun loadArtists(page: Int = 1): CommonResult<List<NmArtistData>> {
        return try {
            val pageSize = 30
            val start = (page - 1) * pageSize
            val end = page * pageSize
            
            val res = kotlin.runCatching {
                DiscoverApi.get()
                    .getArtistList(
                        sort = "name",  // 按名称排序
                        order = "ASC",    // 升序排列
                        start = start,     // 开始索引
                        end = end          // 结束索引
                    )
            }
            val artistDataList = res.getOrThrow()

            _artists.value = artistDataList
            CommonResult.success(artistDataList)
        } catch (e: Exception) {
            CommonResult.fail(msg = e.message)
        }
    }
}