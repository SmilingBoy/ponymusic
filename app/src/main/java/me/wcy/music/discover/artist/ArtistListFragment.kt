package me.wcy.music.discover.artist

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import me.wcy.music.common.BaseMusicFragment
import me.wcy.music.consts.RoutePath
import me.wcy.music.databinding.FragmentArtistListBinding
import me.wcy.music.discover.artist.item.ArtistItemBinder
import me.wcy.music.discover.artist.viewmodel.ArtistListViewModel
import me.wcy.music.discover.artist.bean.NmArtistData
import me.wcy.radapter3.RAdapter
import me.wcy.router.annotation.Route
import top.wangchenyan.common.ext.viewBindings

/**
 * 歌手列表
 */
@Route(RoutePath.ARTIST_LIST)
@AndroidEntryPoint
class ArtistListFragment : BaseMusicFragment() {
    private val viewBinding by viewBindings<FragmentArtistListBinding>()
    private val viewModel by viewModels<ArtistListViewModel>()
    private val adapter by lazy { RAdapter<NmArtistData>() }
    private var page = 1

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
        viewBinding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 注册ArtistItemBinder
        adapter.register(ArtistItemBinder(object : ArtistItemBinder.OnItemClickListener {
            override fun onItemClick(item: NmArtistData) {
                // 歌手点击事件处理
            }

            override fun onMoreClick(item: NmArtistData) {

            }
        }))

        viewBinding.recyclerView.adapter = adapter
    }

    private fun loadData() {
        if (page == 1) {
            showLoadSirLoading()
        }
        lifecycleScope.launch {
            val result = viewModel.loadArtists(page)
            if (result.isSuccess()) {
                if (page == 1) {
                    showLoadSirSuccess()
                    adapter.refresh(emptyList())
                }
                adapter.addAll(result.data ?: emptyList())
                viewBinding.refreshLayout.finishLoadMore()
                viewBinding.refreshLayout.finishRefresh()
            } else {
                showLoadSirError(result.msg)
            }
        }
    }
}