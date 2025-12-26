package me.wcy.music.discover.recommend.song

import android.view.View
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.CacheDiskStaticUtils
import com.blankj.utilcode.util.CacheDiskUtils
import com.blankj.utilcode.util.TimeUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import me.wcy.music.R
import me.wcy.music.common.BaseMusicFragment
import me.wcy.music.common.OnItemClickListener2
import me.wcy.music.common.dialog.songmenu.SongMoreMenuDialog
import me.wcy.music.common.dialog.songmenu.items.CollectMenuItem
import me.wcy.music.consts.RoutePath
import me.wcy.music.databinding.FragmentRecommendSongBinding
import me.wcy.music.discover.DiscoverApi
import me.wcy.music.discover.playlist.detail.bean.NmSongData
import me.wcy.music.discover.recommend.song.item.RecommendSongItemBinder
import me.wcy.music.service.PlayerController
import me.wcy.music.utils.toNmMediaItem
import me.wcy.radapter3.RAdapter
import me.wcy.router.CRouter
import me.wcy.router.annotation.Route
import top.wangchenyan.common.ext.getColor
import top.wangchenyan.common.ext.viewBindings
import javax.inject.Inject

/**
 * Created by wangchenyan.top on 2023/9/15.
 */
@Route(RoutePath.RECOMMEND_SONG, needLogin = true)
@AndroidEntryPoint
class RecommendSongFragment : BaseMusicFragment() {
    private val viewBinding by viewBindings<FragmentRecommendSongBinding>()
    private val adapter by lazy {
        RAdapter<NmSongData>()
    }

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

        configWindowInsets {
            navBarColor = getColor(R.color.play_bar_bg)
        }

        adapter.register(RecommendSongItemBinder(object : OnItemClickListener2<NmSongData> {
            override fun onItemClick(item: NmSongData, position: Int) {
                val entityList = adapter.getDataList().map {
                    it.toNmMediaItem()
                }
                playerController.replaceAll(entityList, entityList[position])
                CRouter.with(requireContext()).url(RoutePath.PLAYING).start()
            }

            override fun onMoreClick(item: NmSongData, position: Int) {
                SongMoreMenuDialog(requireActivity(), item)
                    .setItems(
                        listOf(
//                            CollectMenuItem(lifecycleScope, item),
//                            CommentMenuItem(item),
//                            ArtistMenuItem(item),
//                            AlbumMenuItem(item)
                        )
                    )
                    .show()
            }
        }))
        viewBinding.recyclerView.adapter = adapter
        viewBinding.tvPlayAll.setOnClickListener {
            val entityList = adapter.getDataList().map {
                it.toNmMediaItem()
            }
            playerController.replaceAll(entityList, entityList.first())
            CRouter.with(requireContext()).url(RoutePath.PLAYING).start()
        }

        loadData()
    }

    private fun loadData() {
        var seedTime = CacheDiskStaticUtils.getString("recommend_song_seed", "0").toLong()

        if (!TimeUtils.isToday(seedTime)){
            seedTime = System.currentTimeMillis()
            CacheDiskStaticUtils.put("recommend_song_seed", seedTime.toString())
        }

        lifecycleScope.launch {
            showLoadSirLoading()
            val res = kotlin.runCatching {
                DiscoverApi.get()
                    .getSongList(
                        seed = seedTime.toString(),
                        sort = "random",
                        start = 0,
                        end = 30
                    )
            }
            if (res.isSuccess) {
                showLoadSirSuccess()
                adapter.refresh(res.getOrThrow())
            } else {
                showLoadSirError()
            }
        }
    }
}
