package me.wcy.music.discover.album.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.wcy.music.discover.DiscoverApi
import me.wcy.music.discover.playlist.detail.bean.NmAlbumData
import top.wangchenyan.common.model.CommonResult
import javax.inject.Inject

/**
 * 专辑列表ViewModel
 */
@HiltViewModel
class AlbumListViewModel @Inject constructor() : ViewModel() {
    private val _albums = MutableStateFlow<List<NmAlbumData>>(emptyList())
    val albums: StateFlow<List<NmAlbumData>> = _albums.asStateFlow()

    suspend fun loadAlbums(page: Int = 1): CommonResult<List<NmAlbumData>> {
        return try {
            val pageSize = 30
            val start = (page - 1) * pageSize
            val end = page * pageSize
            
            val res = kotlin.runCatching {
                DiscoverApi.get()
                    .getAlbumList(
                        sort = "title",  // 按标题排序
                        order = "ASC",    // 升序排列
                        start = start,     // 开始索引
                        end = end          // 结束索引
                    )
            }
            val albumDataList = res.getOrThrow()

            _albums.value = albumDataList
            CommonResult.success(albumDataList)
        } catch (e: Exception) {
            CommonResult.fail(msg = e.message)
        }
    }
}