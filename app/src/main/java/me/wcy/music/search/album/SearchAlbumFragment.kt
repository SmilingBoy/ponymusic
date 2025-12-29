package me.wcy.music.search.album

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.wcy.music.common.OnItemClickListener2
import me.wcy.music.common.SimpleMusicRefreshFragment
import me.wcy.music.common.dialog.songmenu.SongMoreMenuDialog
import me.wcy.music.consts.Consts
import me.wcy.music.consts.RoutePath
import me.wcy.music.discover.DiscoverApi
import me.wcy.music.discover.artist.bean.NmArtistData
import me.wcy.music.discover.playlist.detail.bean.NmAlbumData
import me.wcy.music.search.SearchViewModel
import me.wcy.music.search.song.SearchSongItemBinder
import me.wcy.music.service.PlayerController
import me.wcy.music.utils.toNmMediaItem
import me.wcy.radapter3.RAdapter
import me.wcy.router.CRouter
import top.wangchenyan.common.model.CommonResult
import javax.inject.Inject

/**
 * Created by wangchenyan.top on 2023/9/20.
 */
@AndroidEntryPoint
class SearchAlbumFragment : SimpleMusicRefreshFragment<NmAlbumData>() {
    private val viewModel by activityViewModels<SearchViewModel>()
    private val itemBinder by lazy {
        SearchAlbumItemBinder(object : OnItemClickListener2<NmAlbumData> {
            override fun onItemClick(item: NmAlbumData, position: Int) {
//                playerController.addAndPlay(item.toNmMediaItem())
//                CRouter.with(context).url(RoutePath.PLAYING).start()
            }

            override fun onMoreClick(item: NmAlbumData, position: Int) {
//                SongMoreMenuDialog(requireActivity(), item)
//                    .setItems(
//                        listOf(
//                            CollectMenuItem(lifecycleScope, item),
//                            CommentMenuItem(item),
//                            ArtistMenuItem(item),
//                            AlbumMenuItem(item)
//                        )
//                    )
//                    .show()
            }
        }).apply {
            keywords = viewModel.keywords.value
        }
    }

    @Inject
    lateinit var playerController: PlayerController

    override fun isShowTitle(): Boolean {
        return false
    }

    override fun isRefreshEnabled(): Boolean {
        return false
    }

    override fun onLazyCreate() {
        super.onLazyCreate()
        lifecycleScope.launch {
            viewModel.keywords.collectLatest {
                if (it.isNotEmpty()) {
                    showLoadSirLoading()
                    itemBinder.keywords = it
                    autoRefresh(true)
                }
            }
        }
    }

    override fun initAdapter(adapter: RAdapter<NmAlbumData>) {
        adapter.register(itemBinder)
    }

    override suspend fun getData(page: Int): CommonResult<List<NmAlbumData>> {
        val keywords = viewModel.keywords.value
        if (keywords.isEmpty()) {
            return CommonResult.Companion.success(emptyList())
        }
        //1, keywords, "title", (page - 1) * Consts.PAGE_COUNT
        val res = runCatching {
            DiscoverApi.Companion.get().getAlbumList(
                start = (page - 1) * Consts.PAGE_COUNT,
                end = page * Consts.PAGE_COUNT,
                name = keywords,
                sort = "name",
            )
        }
        return if (res.isSuccess) {
            CommonResult.Companion.success(res.getOrThrow())
        } else {
            CommonResult.Companion.fail()
        }
    }
}