package me.wcy.music.v2.album

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import me.wcy.music.common.BaseMusicFragment
import me.wcy.music.databinding.FragmentAlbumListV2Binding
import me.wcy.music.discover.album.viewmodel.AlbumListViewModel
import top.wangchenyan.common.ext.viewBindings

@AndroidEntryPoint
class AlbumListFragment : BaseMusicFragment() {
    private val binding by viewBindings<FragmentAlbumListV2Binding>()
    private val viewModel by viewModels<AlbumListViewModel>()
    private val mAlbumAdapter by lazy { AlbumAdapter() }
    private var page = 1


    override fun getRootView(): View {
        return binding.root
    }

    override fun isUseLoadSir(): Boolean {
        return true
    }

    override fun getLoadSirTarget(): View {
        return binding.content
    }


    override fun onReload() {
        super.onReload()
        loadData()
    }

    override fun onLazyCreate() {
        super.onLazyCreate()

        initView()
        loadData()

        binding.refreshLayout.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
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

    private fun initView() {
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)


        binding.recyclerView.adapter = mAlbumAdapter
    }

    private fun loadData() {
        if (page == 1) {
            showLoadSirLoading()
        }
        lifecycleScope.launch {
            val result = viewModel.loadAlbums(page)
            if (result.isSuccess()) {
                if (page == 1) {
                    showLoadSirSuccess()
                    mAlbumAdapter.submitList(emptyList())
                }
                mAlbumAdapter.addAll(result.data ?: emptyList())
                binding.refreshLayout.finishLoadMore()
                binding.refreshLayout.finishRefresh()
            } else {
                showLoadSirError(result.msg)
            }
        }
    }

}