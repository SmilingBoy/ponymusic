package me.wcy.music.discover.playlist.songlist

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import me.wcy.music.common.BaseMusicFragment
import me.wcy.music.common.dialog.songmenu.SongMoreMenuDialog
import me.wcy.music.consts.RoutePath
import me.wcy.music.databinding.FragmentSonglistBinding
import me.wcy.music.discover.playlist.detail.bean.NmSongData
import me.wcy.music.discover.playlist.songlist.item.SongListItemBinder
import me.wcy.music.discover.playlist.songlist.viewmodel.SongListViewModel
import me.wcy.radapter3.RAdapter
import me.wcy.router.annotation.Route
import top.wangchenyan.common.ext.viewBindings

/**
 * 歌曲列表
 */
@Route(RoutePath.SONG_LIST)
@AndroidEntryPoint
class SongListFragment : BaseMusicFragment() {

    companion object {
        const val SHOW_TYPE_KEY = "showType"
        const val SHOW_TYPE_ALL = 0
        const val SHOW_TYPE_HISTORY = 1
    }

    private val viewBinding by viewBindings<FragmentSonglistBinding>()
    private val viewModel by viewModels<SongListViewModel>()
    private val adapter by lazy { RAdapter<NmSongData>() }
    private var page = 1

    private var showType = SHOW_TYPE_ALL

    override fun getRootView(): View {
        return viewBinding.root
    }

    override fun isUseLoadSir(): Boolean {
        return true
    }

    override fun getLoadSirTarget(): View {
        return viewBinding.content
    }

    override fun onReload() {
        super.onReload()
        loadData()
    }

    override fun onLazyCreate() {
        super.onLazyCreate()

        showType = getRouteArguments().getIntExtra(SHOW_TYPE_KEY, SHOW_TYPE_ALL)

        initTitle()
        initView()
        loadData()

        viewBinding.refreshLayout.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
            override fun onRefresh(refreshLayout: RefreshLayout) {
                page = 1
                loadData()
            }

            override fun onLoadMore(refreshLayout: RefreshLayout) {
                page++
                loadData()
            }

        })
    }

    private fun initTitle() {
    }

    private fun initView() {
        adapter.register(SongListItemBinder(object : SongListItemBinder.OnItemClickListener {
            override fun onItemClick(item: NmSongData) {
                viewModel.playSong(item)
            }

            override fun onMoreClick(item: NmSongData) {
                showSongMoreMenu(item)
            }
        }))
        viewBinding.recyclerView.adapter = adapter
    }

    private fun loadData() {
        if (page == 1) {
            showLoadSirLoading()
        }
        lifecycleScope.launch {
            val result = viewModel.loadSongs(page, showType)
            if (result.isSuccess()) {
                if (page == 1) {
                    showLoadSirSuccess()
                }
                adapter.addAll(result.data ?: emptyList())
                viewBinding.refreshLayout.finishLoadMore()
                viewBinding.refreshLayout.finishRefresh()
            } else {
                showLoadSirError(result.msg)
            }
        }
    }

    private fun showSongMoreMenu(item: NmSongData) {
        SongMoreMenuDialog(requireActivity(), item).show()
    }

}