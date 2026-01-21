package me.wcy.music.v2

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.blankj.utilcode.util.FragmentUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import me.wcy.music.R
import me.wcy.music.common.BaseMusicActivity
import me.wcy.music.databinding.ActivityPlayingV2Binding
import me.wcy.music.v2.song.SongListFragment
import top.wangchenyan.common.ext.viewBindings

@AndroidEntryPoint
class PlayingV2Activity : BaseMusicActivity() {
    private val viewBinding by viewBindings<ActivityPlayingV2Binding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        layoutAInit()

        layoutBBehavior()

        initEvent()
        
        // 默认显示歌曲列表Fragment
        val fragment = SongListFragment()
        FragmentUtils.replace(supportFragmentManager, fragment, R.id.contentFrame)
    }

    /**
     * 侧边栏布局初始化
     */
    private fun layoutAInit() {
        // 设置DrawerLayout监听器，实现主内容跟随抽屉滑动
        viewBinding.layoutA.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                // 获取抽屉宽度
                val drawerWidth = drawerView.width
                // 计算主内容平移距离：抽屉宽度 * 滑动偏移量
                val translateDistance = drawerWidth * slideOffset
                // 设置主内容平移
                viewBinding.contentFrame.translationX = translateDistance
            }

            override fun onDrawerOpened(drawerView: View) {
                // 抽屉打开时的处理
            }

            override fun onDrawerClosed(drawerView: View) {
                // 抽屉关闭时，重置主内容位置
                viewBinding.contentFrame.translationX = 0f
            }

            override fun onDrawerStateChanged(newState: Int) {
                // 抽屉状态变化时的处理
            }
        })
        viewBinding.layoutA.setScrimColor(Color.TRANSPARENT)
        viewBinding.layoutA.setDrawerShadow(null, GravityCompat.START)
        viewBinding.layoutA.setDrawerElevation(0f)

        // 默认展开左侧菜单
//        viewBinding.layoutA.openDrawer(viewBinding.leftMenu.menuLayout)
    }

    private fun layoutBBehavior() {
        val behavior = BottomSheetBehavior.from(viewBinding.layoutB)
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

    private fun initEvent() {

        viewBinding.leftMenu.songLayout.setOnClickListener {
            val fragment = SongListFragment()
            FragmentUtils.replace(supportFragmentManager, fragment, R.id.contentFrame)
        }
    }
}