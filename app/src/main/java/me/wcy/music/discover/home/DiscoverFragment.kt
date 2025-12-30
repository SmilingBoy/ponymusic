package me.wcy.music.discover.home

import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.config.IndicatorConfig
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.wcy.music.R
import me.wcy.music.account.service.UserService
import me.wcy.music.common.ApiDomainDialog
import me.wcy.music.common.BaseMusicFragment
import me.wcy.music.consts.RoutePath
import me.wcy.music.databinding.FragmentDiscoverBinding
import me.wcy.music.discover.banner.BannerData
import me.wcy.music.discover.home.viewmodel.DiscoverViewModel
import me.wcy.music.discover.playlist.detail.bean.NmSongData
import me.wcy.music.discover.playlist.songlist.SongListFragment
import me.wcy.music.discover.playlist.songlist.item.SongVlistItemBinder
import me.wcy.music.main.MainActivity
import me.wcy.music.service.PlayerController
import me.wcy.music.storage.preference.ConfigPreferences
import me.wcy.music.utils.toMediaItem
import me.wcy.music.utils.toNmMediaItem
import me.wcy.radapter3.RAdapter
import me.wcy.router.CRouter
import top.wangchenyan.common.ext.load
import top.wangchenyan.common.ext.viewBindings
import top.wangchenyan.common.utils.LaunchUtils
import top.wangchenyan.common.widget.decoration.SpacingDecoration
import javax.inject.Inject

/**
 * Created by wangchenyan.top on 2023/8/21.
 */
@AndroidEntryPoint
class DiscoverFragment : BaseMusicFragment() {
    private val viewBinding by viewBindings<FragmentDiscoverBinding>()
    private val viewModel by viewModels<DiscoverViewModel>()

    @Inject
    lateinit var userService: UserService

    @Inject
    lateinit var playerController: PlayerController

    private val songHistoryListAdapter by lazy {
        RAdapter<NmSongData>()
    }

    private val mostPlayedListAdapter by lazy {
        RAdapter<NmSongData>()
    }

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
        checkApiDomain(true)
    }

    override fun onLazyCreate() {
        super.onLazyCreate()

        initTitle()
//        initBanner()
        initTopButton()
        initSongHistoryList()
        initMostPlayedList()
        checkApiDomain(false)
    }

    private fun initTitle() {
        getTitleLayout()?.run {
            addImageMenu(
                R.drawable.ic_menu,
                isDayNight = true,
                isLeft = true
            ).setOnClickListener {
                val activity = requireActivity()
                if (activity is MainActivity) {
                    activity.openDrawer()
                }
            }
        }
        getTitleLayout()?.getContentView()?.setOnClickListener {
            if (ApiDomainDialog.checkApiDomain(requireContext())) {
                CRouter.with(requireActivity()).url(RoutePath.SEARCH).start()
            }
        }
    }

    private fun initBanner() {
        viewBinding.banner.addBannerLifecycleObserver(this)
            .setIndicator(CircleIndicator(requireContext()))
            .setIndicatorGravity(IndicatorConfig.Direction.LEFT)
            .setIndicatorMargins(IndicatorConfig.Margins().apply {
                leftMargin = SizeUtils.dp2px(28f)
            })
            .setAdapter(object : BannerImageAdapter<BannerData>(emptyList()) {
                override fun onBindView(
                    holder: BannerImageHolder?,
                    data: BannerData?,
                    position: Int,
                    size: Int
                ) {
                    holder?.imageView?.apply {
                        val padding = SizeUtils.dp2px(16f)
                        setPadding(padding, 0, padding, 0)
                        load(data?.pic ?: "", SizeUtils.dp2px(12f))
                        setOnClickListener {
                            data ?: return@setOnClickListener
                            if (data.song != null) {
                                playerController.addAndPlay(data.song.toMediaItem())
                                CRouter.with(context).url(RoutePath.PLAYING).start()
                            } else if (data.url.isNotEmpty()) {
                                LaunchUtils.launchBrowser(requireContext(), data.url)
                            } else if (data.targetId > 0) {
                                CRouter.with(requireActivity())
                                    .url(RoutePath.PLAYLIST_DETAIL)
                                    .extra("id", data.targetId)
                                    .start()
                            }
                        }
                    }
                }
            })
        lifecycleScope.launch {
            viewModel.bannerList.collectLatest {
                viewBinding.banner.isVisible = it.isNotEmpty()
                viewBinding.bannerPlaceholder.isVisible = it.isEmpty()
                if (it.isNotEmpty()) {
                    viewBinding.banner.setDatas(it)
                }
            }
        }
    }

    private fun initTopButton() {
        //每日推荐
        viewBinding.btnRecommendSong.setOnClickListener {
            CRouter.with(requireActivity()).url(RoutePath.RECOMMEND_SONG).start()
        }

        // 专辑列表
        viewBinding.btnPrivateFm.setOnClickListener {
            CRouter.with(requireActivity())
                .url(RoutePath.ALBUM_LIST)
                .start()
        }

        // 歌曲列表
        viewBinding.btnRecommendPlaylist.setOnClickListener {
            CRouter.with(requireActivity())
                .url(RoutePath.SONG_LIST)
                .start()
        }

        // 歌手列表
        viewBinding.btnRank.setOnClickListener {
            CRouter.with(requireActivity()).url(RoutePath.ARTIST_LIST).start()
        }
    }

    private fun initSongHistoryList() {
        viewBinding.tvSongHistory.setOnClickListener {
            CRouter.with(requireActivity())
                .url(RoutePath.SONG_LIST)
                .extra(SongListFragment.SHOW_TYPE_KEY, SongListFragment.SHOW_TYPE_HISTORY)
                .start()
        }
        val itemWidth = ((ScreenUtils.getAppScreenWidth() - SizeUtils.dp2px(20f)) / 3)
            .coerceAtMost(resources.getDimensionPixelSize(R.dimen.playlist_item_max_width))
        songHistoryListAdapter.register(SongVlistItemBinder(itemWidth, true, object :
            SongVlistItemBinder.OnItemClickListener {
            override fun onItemClick(item: NmSongData) {
                playerController.addAndPlay(item.toNmMediaItem())
            }

            override fun onPlayClick(item: NmSongData) {
                playerController.addAndPlay(item.toNmMediaItem())
            }
        }))
        viewBinding.rvSongHostoryList.adapter = songHistoryListAdapter
        viewBinding.rvSongHostoryList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        viewBinding.rvSongHostoryList.addItemDecoration(
            SpacingDecoration(SizeUtils.dp2px(10f))
        )

        val updateVisibility = {
            if (userService.isLogin() && viewModel.songHistoryList.value.isNotEmpty()) {
                viewBinding.tvSongHistory.isVisible = true
                viewBinding.rvSongHostoryList.isVisible = true
            } else {
                viewBinding.tvSongHistory.isVisible = false
                viewBinding.rvSongHostoryList.isVisible = false
            }
        }

        lifecycleScope.launch {
            userService.profile.collectLatest {
                updateVisibility()
            }
        }

        lifecycleScope.launch {
            viewModel.songHistoryList.collectLatest { songList ->
                updateVisibility()
                songHistoryListAdapter.refresh(songList)
            }
        }
    }

    private fun initMostPlayedList() {
        viewBinding.tvMostPlayed.setOnClickListener {
            CRouter.with(requireActivity())
                .url(RoutePath.SONG_LIST)
                .extra(SongListFragment.SHOW_TYPE_KEY, SongListFragment.SHOW_TYPE_MOST_PLAYED)
                .start()
        }
        val itemWidth = ((ScreenUtils.getAppScreenWidth() - SizeUtils.dp2px(20f)) / 3)
            .coerceAtMost(resources.getDimensionPixelSize(R.dimen.playlist_item_max_width))
        mostPlayedListAdapter.register(SongVlistItemBinder(itemWidth, true, object :
            SongVlistItemBinder.OnItemClickListener {
            override fun onItemClick(item: NmSongData) {
                playerController.addAndPlay(item.toNmMediaItem())
            }

            override fun onPlayClick(item: NmSongData) {
                playerController.addAndPlay(item.toNmMediaItem())
            }
        }))
        viewBinding.rvMostPlayedList.adapter = mostPlayedListAdapter
        viewBinding.rvMostPlayedList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        viewBinding.rvMostPlayedList.addItemDecoration(
            SpacingDecoration(SizeUtils.dp2px(10f))
        )

        val updateVisibility = {
            if (userService.isLogin() && viewModel.mostPlayedList.value.isNotEmpty()) {
                viewBinding.tvMostPlayed.isVisible = true
                viewBinding.rvMostPlayedList.isVisible = true
            } else {
                viewBinding.tvMostPlayed.isVisible = false
                viewBinding.rvMostPlayedList.isVisible = false
            }
        }

        lifecycleScope.launch {
            userService.profile.collectLatest {
                updateVisibility()
            }
        }

        lifecycleScope.launch {
            viewModel.mostPlayedList.collectLatest { songList ->
                updateVisibility()
                mostPlayedListAdapter.refresh(songList)
            }
        }
    }

    private fun checkApiDomain(isReload: Boolean) {
        if (ConfigPreferences.apiDomain.isNotEmpty()) {
            showLoadSirSuccess()
        } else {
            showLoadSirError("请先设置音乐API域名")
            if (isReload) {
                ApiDomainDialog.checkApiDomain(requireContext())
            }
        }
    }

}