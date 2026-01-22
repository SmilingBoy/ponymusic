package me.wcy.music.v2.artist

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import me.wcy.music.common.BaseMusicFragment
import me.wcy.music.databinding.FragmentArtistListV2Binding
import me.wcy.music.discover.artist.viewmodel.ArtistListViewModel
import top.wangchenyan.common.ext.viewBindings

@AndroidEntryPoint
class ArtistListFragment : BaseMusicFragment() {
    private val viewBinding by viewBindings<FragmentArtistListV2Binding>()
    private val viewModel by viewModels<ArtistListViewModel>()
    private val mArtistAdapter by lazy { ArtistAdapter() }
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

        viewBinding.recyclerView.adapter = mArtistAdapter
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
                    mArtistAdapter.submitList(emptyList())
                }
                mArtistAdapter.addAll(result.data ?: emptyList())
                viewBinding.refreshLayout.finishLoadMore()
                viewBinding.refreshLayout.finishRefresh()
            } else {
                showLoadSirError(result.msg)
            }
        }
    }
}