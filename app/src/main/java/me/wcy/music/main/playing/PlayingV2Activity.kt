package me.wcy.music.main.playing

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import me.wcy.music.common.BaseMusicActivity
import me.wcy.music.databinding.ActivityPlayingV2Binding
import top.wangchenyan.common.ext.viewBindings

@AndroidEntryPoint
class PlayingV2Activity : BaseMusicActivity() {
    private val viewBinding by viewBindings<ActivityPlayingV2Binding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        val behavior = BottomSheetBehavior.from(viewBinding.layoutB)

        // 默认展开左侧菜单
        viewBinding.layoutA.openDrawer(viewBinding.leftMenu.menuLayout)

        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val alpha = 1 - slideOffset
                viewBinding.llMusicBar.alpha = alpha

                // 当完全透明时，设置为Invisible
                if (alpha <= 0f) {
                    viewBinding.llMusicBar.visibility = View.INVISIBLE
                } else {
                    viewBinding.llMusicBar.visibility = View.VISIBLE
                }
            }
        })

    }
}