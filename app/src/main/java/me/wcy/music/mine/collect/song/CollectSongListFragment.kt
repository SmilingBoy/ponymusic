package me.wcy.music.mine.collect.song

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.LogUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.wcy.music.common.BaseMusicFragment
import me.wcy.music.common.dialog.songmenu.SongMoreMenuDialog
import me.wcy.music.consts.RoutePath
import me.wcy.music.databinding.FragmentCollectSongListBinding
import me.wcy.music.discover.playlist.detail.bean.NmSongData
import me.wcy.music.mine.collect.song.viewmodel.CollectSongListViewModel
import me.wcy.music.service.PlayerController
import me.wcy.music.utils.toNmMediaItem
import me.wcy.radapter3.RAdapter
import me.wcy.router.CRouter
import me.wcy.router.annotation.Route
import top.wangchenyan.common.ext.viewBindings
import javax.inject.Inject

/**
 * 收藏歌曲列表
 */
@Route(RoutePath.COLLECT_SONG_LIST)
@AndroidEntryPoint
class CollectSongListFragment : BaseMusicFragment() {
    private val viewBinding by viewBindings<FragmentCollectSongListBinding>()
    private val viewModel by viewModels<CollectSongListViewModel>()
    private val adapter by lazy { RAdapter<NmSongData>() }

    @Inject
    lateinit var playerController: PlayerController
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
        lifecycleScope.launch {
            viewModel.collectSongs.collectLatest {
                LogUtils.d("收藏歌曲列表2：${it.size}")
                adapter.refresh(it)
                showLoadSirSuccess()

            }
        }
        viewBinding.tvPlayAll.setOnClickListener {
            val entityList = adapter.getDataList().map {
                it.toNmMediaItem()
            }
            playerController.replaceAll(entityList, entityList.first())
            CRouter.with(requireContext()).url(RoutePath.PLAYING).start()
        }
    }

    private fun initTitle() {
    }

    private fun initView() {
        adapter.register(CollectSongItemBinder(object : CollectSongItemBinder.OnItemClickListener {
            override fun onItemClick(item: NmSongData, position: Int) {
                viewModel.playSong(item)
            }

            override fun onMoreClick(item: NmSongData, position: Int) {
                showSongMoreMenu(item)
            }
        }))
        viewBinding.recyclerView.adapter = adapter
    }

    private fun loadData() {
        viewModel.loadCollectSongs()
    }

    private fun showSongMoreMenu(item: NmSongData) {
        SongMoreMenuDialog(requireActivity(), item).show()
    }
}