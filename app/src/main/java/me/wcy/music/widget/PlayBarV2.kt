package me.wcy.music.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.text.buildSpannedString
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.LogUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.wcy.music.R
import me.wcy.music.consts.RoutePath
import me.wcy.music.databinding.LayoutPlayBarV2Binding
import me.wcy.music.main.playlist.CurrentPlaylistFragment
import me.wcy.music.service.PlayServiceModule.playerController
import me.wcy.music.service.PlayState
import me.wcy.music.utils.getSmallCover
import me.wcy.router.CRouter
import top.wangchenyan.common.CommonApp
import top.wangchenyan.common.ext.findActivity
import top.wangchenyan.common.ext.findLifecycleOwner
import top.wangchenyan.common.ext.loadAvatar


/**
 * playbar v2
 */
class PlayBarV2 @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    private val viewBinding: LayoutPlayBarV2Binding
    private val playerController by lazy {
        CommonApp.app.playerController()
    }

    init {
        id = R.id.musicBar
        viewBinding = LayoutPlayBarV2Binding.inflate(LayoutInflater.from(context), this, true)

        initView()
        context.findLifecycleOwner()?.let {
            initData(it)
        }
    }

    private fun initView() {
        viewBinding.root.setOnClickListener {
            CRouter.with(context).url(RoutePath.PLAYING).start()
        }
        viewBinding.ivPlay.setOnClickListener {
            playerController.playPause()
        }
//        viewBinding.ivNext.setOnClickListener {
//            playerController.next()
//        }
        viewBinding.ivPlaylist.setOnClickListener {
            val activity = context.findActivity()
            if (activity is FragmentActivity) {
                CurrentPlaylistFragment.newInstance()
                    .show(activity.supportFragmentManager, CurrentPlaylistFragment.TAG)
            }
        }
    }

    private fun initData(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycleScope.launch {
            playerController.currentSong.collectLatest { currentSong ->
                if (currentSong != null) {
                    isVisible = true
                    viewBinding.ivCover.loadAvatar(currentSong.getSmallCover(), corners = 8)
                    viewBinding.tvTitle.text = buildSpannedString {
                        append(currentSong.mediaMetadata.title?.split("/")?.last())
                    }
                } else {
                    isVisible = false
                }
            }
        }

        lifecycleOwner.lifecycleScope.launch {
            playerController.playState.collectLatest { playState ->
                when (playState) {
                    PlayState.Preparing -> {
                        viewBinding.ivPlay.isSelected = false
                    }

                    PlayState.Playing -> {
                        viewBinding.ivPlay.isSelected = true
                    }

                    else -> {
                        viewBinding.ivPlay.isSelected = false
                    }
                }

            }
        }

    }
}