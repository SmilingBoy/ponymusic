package me.wcy.music.v2.song

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import me.wcy.music.common.BaseMusicFragment
import me.wcy.music.databinding.FragmentSonglistV2Binding
import me.wcy.music.discover.playlist.songlist.OrderWayEnums
import me.wcy.music.discover.playlist.songlist.SongOrderEnums
import me.wcy.music.discover.playlist.songlist.viewmodel.SongListViewModel
import top.wangchenyan.common.ext.viewBindings

@AndroidEntryPoint
class SongListFragment : BaseMusicFragment() {


    private val binding by viewBindings<FragmentSonglistV2Binding>()
    private val songAdapter = SongAdapter()
    private val viewModel by viewModels<SongListViewModel>()
    private var page = 1
    override fun getRootView(): View {
        return binding.root
    }

    override fun isUseLoadSir(): Boolean {
        return true
    }

    override fun onReload() {
        super.onReload()
        loadData()
    }

    override fun onLazyCreate() {
        super.onLazyCreate()

        initViews()

        loadData()
    }

    private fun initViews() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = songAdapter

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

        songAdapter.setOnItemClickListener { adapter, view, position ->
            viewModel.playSong(adapter.items[position])
        }
    }

    private fun loadData() {
        if (page == 1) {
            showLoadSirLoading()
        }
        lifecycleScope.launch {
            val result = viewModel.loadSongs(page, SongOrderEnums.TITLE, OrderWayEnums.ASC)
            if (result.isSuccess()) {
                if (page == 1) {
                    showLoadSirSuccess()
                }
                songAdapter.addAll(result.data ?: emptyList())
                binding.refreshLayout.finishLoadMore()
                binding.refreshLayout.finishRefresh()
            } else {
                showLoadSirError(result.msg)
            }
        }
    }

}