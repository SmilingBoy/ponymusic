package me.wcy.music.v2

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.FragmentUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SizeUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import me.wcy.music.R
import me.wcy.music.account.service.UserService
import me.wcy.music.common.BaseMusicActivity
import me.wcy.music.common.DarkModeService
import me.wcy.music.databinding.ActivityPlayingV2Binding
import me.wcy.music.service.PlayServiceModule
import me.wcy.music.service.PlayServiceModule.playerController
import me.wcy.music.storage.preference.ConfigPreferences
import me.wcy.music.v2.album.AlbumListFragment
import me.wcy.music.v2.artist.ArtistListFragment
import me.wcy.music.v2.song.SongListFragment
import top.wangchenyan.common.ext.showConfirmDialog
import top.wangchenyan.common.ext.viewBindings
import javax.inject.Inject

@AndroidEntryPoint
class PlayingV2Activity : BaseMusicActivity() {
    private val viewBinding by viewBindings<ActivityPlayingV2Binding>()

    // 用户服务，用于处理用户相关操作（通过Hilt注入）
    @Inject
    lateinit var userService: UserService

    @Inject
    lateinit var darkModeService: DarkModeService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // 观察播放器准备状态，当播放器准备就绪后才初始化界面
        PlayServiceModule.isPlayerReady.observe(this) { isReady ->
            if (isReady) {
                setContentView(viewBinding.root)


                layoutLeftMenuInit()

                layoutHeaderInit()

                layoutAInit()

                layoutBBehavior()

                initEvent()

                // 默认显示歌曲列表Fragment
                viewBinding.tvTitle.text = "歌曲"
                val fragment = SongListFragment()
                FragmentUtils.replace(supportFragmentManager, fragment, R.id.contentFrame)
            }
        }

    }

    private fun layoutLeftMenuInit() {

        BarUtils.addMarginTopEqualStatusBarHeight(viewBinding.leftMenu.llTop)

        val themeViews = listOf(
            viewBinding.leftMenu.themeLight,
            viewBinding.leftMenu.themeDark,
            viewBinding.leftMenu.themeSystem
        )
        val darkMode = DarkModeService.DarkMode.fromValue(ConfigPreferences.darkMode)
        themeViews.forEach { it.isSelected = false }
        when (darkMode) {
            DarkModeService.DarkMode.Light -> {
                viewBinding.leftMenu.themeLight.isSelected = true
            }

            DarkModeService.DarkMode.Dark -> {
                viewBinding.leftMenu.themeDark.isSelected = true
            }

            DarkModeService.DarkMode.Auto -> {
                viewBinding.leftMenu.themeSystem.isSelected = true
            }
        }

        // 主题切换
        viewBinding.leftMenu.themeSystem.setOnClickListener {
            darkModeService.setDarkMode(DarkModeService.DarkMode.Auto)
            themeViews.forEach { t -> t.isSelected = false }
            it.isSelected = true
        }

        viewBinding.leftMenu.themeLight.setOnClickListener {
            darkModeService.setDarkMode(DarkModeService.DarkMode.Light)
            themeViews.forEach { t -> t.isSelected = false }
            it.isSelected = true
        }

        viewBinding.leftMenu.themeDark.setOnClickListener {
            darkModeService.setDarkMode(DarkModeService.DarkMode.Dark)
            themeViews.forEach { t -> t.isSelected = false }
            it.isSelected = true
        }

        // 主题模式布局显示/隐藏切换
        viewBinding.leftMenu.changeThemeMode.setOnClickListener {
            toggleThemeModeLayout()
        }

        // 退出应用
        viewBinding.leftMenu.exitApp.setOnClickListener {
            exitApp()
        }


        viewBinding.leftMenu.themeModeLayout.post {
            LogUtils.d("themeModeLayout height: ${viewBinding.leftMenu.themeModeLayout.height}")
        }
    }

    /**
     * 切换主题模式布局的显示/隐藏状态，并添加动画效果
     */
    private fun toggleThemeModeLayout() {
        val themeModeLayout = viewBinding.leftMenu.themeModeLayout
        val changeThemeModeIcon = viewBinding.leftMenu.changeThemeMode
        
        if (themeModeLayout.isVisible) {
            // 隐藏时：向上收起动画（通过改变高度实现）
            // 同时：顺时针旋转一周
            changeThemeModeIcon.animate()
                .rotation(360f)
                .setDuration(300)
                .start()
            
            val heightAnimator = ValueAnimator.ofInt(themeModeLayout.height, 0)
            heightAnimator.duration = 300
            heightAnimator.addUpdateListener { animation ->
                val height = animation.animatedValue as Int
                val layoutParams = themeModeLayout.layoutParams
                layoutParams.height = height
                themeModeLayout.layoutParams = layoutParams
            }
            heightAnimator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    themeModeLayout.visibility = View.GONE
                    changeThemeModeIcon.rotation = 0f // 重置旋转角度
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
            heightAnimator.start()
        } else {
            // 显示时：向下展开动画（通过改变高度实现）
            // 同时：逆时针旋转一周
            changeThemeModeIcon.animate()
                .rotation(-360f)
                .setDuration(300)
                .start()
            
            // 先测量真实高度
            themeModeLayout.visibility = View.VISIBLE
            themeModeLayout.measure(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
//            val targetHeight = themeModeLayout.measuredHeight
            // 48 * 3F + 4 * 3F = 144dp 507 546
            val targetHeight = SizeUtils.dp2px(48 * 3F + 4 * 3F + 16F)
            LogUtils.d("真实高度：$targetHeight")

            // 初始高度设为0
            val layoutParams = themeModeLayout.layoutParams
            layoutParams.height = 0
            themeModeLayout.layoutParams = layoutParams

            // 执行高度动画
            val heightAnimator = ValueAnimator.ofInt(0, targetHeight)
            heightAnimator.duration = 300
            heightAnimator.addUpdateListener { animation ->
                val height = animation.animatedValue as Int
                val params = themeModeLayout.layoutParams
                params.height = height
                themeModeLayout.layoutParams = params
            }
            heightAnimator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    changeThemeModeIcon.rotation = 0f // 重置旋转角度
                }
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
            heightAnimator.start()
        }
    }

    private fun layoutHeaderInit() {

        BarUtils.addMarginTopEqualStatusBarHeight(viewBinding.flHeader)
        viewBinding.ivMenu.setOnClickListener {
            if (viewBinding.layoutA.isDrawerOpen(GravityCompat.START)) {
                viewBinding.layoutA.closeDrawer(GravityCompat.START)
            } else {
                viewBinding.layoutA.openDrawer(GravityCompat.START)
            }
        }
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
                viewBinding.container.translationX = translateDistance
            }

            override fun onDrawerOpened(drawerView: View) {
                // 抽屉打开时的处理
            }

            override fun onDrawerClosed(drawerView: View) {
                // 抽屉关闭时，重置主内容位置
                viewBinding.container.translationX = 0f
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
            viewBinding.layoutA.closeDrawer(GravityCompat.START)
            viewBinding.tvTitle.text = "歌曲"
            val fragment = SongListFragment()
            FragmentUtils.replace(supportFragmentManager, fragment, R.id.contentFrame)
        }

        viewBinding.leftMenu.albumLayout.setOnClickListener {
            viewBinding.layoutA.closeDrawer(GravityCompat.START)
            viewBinding.tvTitle.text = "专辑"
            val fragment = AlbumListFragment()
            FragmentUtils.replace(supportFragmentManager, fragment, R.id.contentFrame)
        }

        viewBinding.leftMenu.artistLayout.setOnClickListener {
            viewBinding.layoutA.closeDrawer(GravityCompat.START)
            viewBinding.tvTitle.text = "艺术家"
            val fragment = ArtistListFragment()
            FragmentUtils.replace(supportFragmentManager, fragment, R.id.contentFrame)
        }
    }


    /**
     * 退出登录
     * 显示确认对话框，用户确认后调用用户服务执行退出登录操作
     */
    private fun logout() {
        showConfirmDialog(message = "确认退出登录？") {
            lifecycleScope.launch {
                // 调用用户服务执行退出登录
                userService.logout()
            }
        }
    }

    /**
     * 退出应用
     * 停止播放器并结束当前Activity
     */
    private fun exitApp() {
        // 停止音乐播放
        application.playerController().stop()
        // 结束当前Activity
        finish()
    }
}