package me.wcy.music.main

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.wcy.music.R
import me.wcy.music.account.service.UserService
import me.wcy.music.common.ApiDomainDialog
import me.wcy.music.common.BaseMusicActivity
import me.wcy.music.consts.RoutePath
import me.wcy.music.databinding.ActivityMainBinding
import me.wcy.music.databinding.NavigationHeaderBinding
import me.wcy.music.databinding.TabItemBinding
import me.wcy.music.service.MusicService
import me.wcy.music.service.PlayServiceModule
import me.wcy.music.service.PlayServiceModule.playerController
import me.wcy.music.utils.QuitTimer
import me.wcy.music.utils.TimeUtils
import me.wcy.router.CRouter
import top.wangchenyan.common.ext.getColorEx
import top.wangchenyan.common.ext.showConfirmDialog
import top.wangchenyan.common.ext.toast
import top.wangchenyan.common.ext.viewBindings
import top.wangchenyan.common.widget.pager.CustomTabPager
import javax.inject.Inject

/**
 * 应用主界面
 * 包含底部标签页切换、侧边抽屉菜单、播放定时器等功能
 * 负责管理应用的主要导航和用户交互
 * Created by wangchenyan.top on 2023/8/21.
 */
@AndroidEntryPoint
class MainActivity : BaseMusicActivity() {
    // 视图绑定，用于访问布局文件中的所有视图
    private val viewBinding by viewBindings<ActivityMainBinding>()
    // 退出定时器，用于设置自动退出应用的时间
    private val quitTimer by lazy {
        QuitTimer(onTimerListener)
    }
    // 定时器菜单项，用于显示定时器状态
    private var timerItem: MenuItem? = null

    // 用户服务，用于处理用户相关操作（通过Hilt注入）
    @Inject
    lateinit var userService: UserService

    /**
     * Activity创建时调用的方法
     * 初始化界面布局、底部标签页、抽屉菜单等
     * @param savedInstanceState 保存的实例状态，用于恢复Activity状态
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 观察播放器准备状态，当播放器准备就绪后才初始化界面
        PlayServiceModule.isPlayerReady.observe(this) { isReady ->
            if (isReady) {
                // 设置布局内容
                setContentView(viewBinding.root)

                // 创建自定义标签页管理器，配置底部标签页
                CustomTabPager(lifecycle, supportFragmentManager, viewBinding.viewPager).apply {
                    // 遍历所有导航标签，为每个标签添加对应的Fragment
                    NaviTab.ALL.onEach {
                        val tabItem = getTabItem(it.icon, it.name)
                        addFragment(it.newFragment(), tabItem.root)
                    }
                    // 设置标签页不可滚动
                    setScrollable(false)
                    // 完成标签页设置
                    setup()
                }

                // 初始化抽屉菜单
                initDrawer()
                // 解析Intent参数
                parseIntent()
            }
        }

        // 配置窗口内边距，设置导航栏颜色
        configWindowInsets {
            navBarColor = getColorEx(R.color.tab_bg)
        }
    }

    /**
     * 当Activity被重新启动时调用的方法
     * 处理新的Intent参数
     * @param intent 新的Intent参数
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // 更新当前Activity的Intent
        setIntent(intent)
        // 解析新的Intent参数
        parseIntent()
    }

    /**
     * 初始化抽屉菜单
     * 添加头部布局，设置菜单项选择监听器，并根据用户登录状态控制退出登录菜单项的显示
     */
    private fun initDrawer() {
        // 填充抽屉菜单头部布局
        val navigationHeaderBinding = NavigationHeaderBinding.inflate(
            LayoutInflater.from(this),
            viewBinding.navigationView,
            false
        )
        // 添加头部布局到抽屉菜单
        viewBinding.navigationView.addHeaderView(navigationHeaderBinding.root)
        // 设置抽屉菜单项选择监听器
        viewBinding.navigationView.setNavigationItemSelectedListener(onMenuSelectListener)
        // 启动协程观察用户登录状态
        lifecycleScope.launch {
//            userService.profile.collectLatest { profile ->
//                // 根据用户登录状态控制退出登录菜单项的显示
//                val menuLogout = viewBinding.navigationView.menu.findItem(R.id.action_logout)
//                menuLogout.isVisible = profile != null
//            }
            userService.navidromeProfile.collectLatest { profile ->
                // 根据用户登录状态控制退出登录菜单项的显示
                val menuLogout = viewBinding.navigationView.menu.findItem(R.id.action_logout)
                menuLogout.isVisible = profile != null
            }
        }
    }

    /**
     * 打开抽屉菜单
     * 如果抽屉菜单当前处于关闭状态，则打开它
     */
    fun openDrawer() {
        if (viewBinding.drawerLayout.isDrawerOpen(GravityCompat.START).not()) {
            viewBinding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    /**
     * 解析Intent参数
     * 处理从通知栏点击进入应用的情况
     */
    private fun parseIntent() {
        val intent = intent
        // 检查是否从通知栏点击进入应用
        if (intent.hasExtra(MusicService.EXTRA_NOTIFICATION)) {
            // 如果当前有正在播放的歌曲，跳转到播放界面
            if (application.playerController().currentSong.value != null) {
                CRouter.with(this).url(RoutePath.PLAYING).start()
            }
            // 重置Intent，避免重复处理
            setIntent(Intent())
        }
    }

    /**
     * 抽屉菜单选择监听器
     * 处理抽屉菜单中各菜单项的点击事件
     */
    private val onMenuSelectListener = object : NavigationView.OnNavigationItemSelectedListener {
        /**
         * 菜单项被点击时调用
         * @param item 被点击的菜单项
         * @return 是否处理了该点击事件
         */
        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            // 关闭抽屉菜单
            viewBinding.drawerLayout.closeDrawers()
            // 延迟1秒后取消菜单项的选中状态
            lifecycleScope.launch {
                delay(1000)
                item.isChecked = false
            }
            // 根据菜单项的ID处理不同的点击事件
            when (item.itemId) {
                R.id.action_domain_setting -> {
                    // 显示API域名设置对话框
                    ApiDomainDialog(this@MainActivity).show()
                    return true
                }

                R.id.action_setting -> {
                    // 跳转到设置界面
                    CRouter.with(this@MainActivity).url("/settings").start()
                    return true
                }

                R.id.action_timer -> {
                    // 显示定时器设置对话框
                    timerDialog()
                    return true
                }

                R.id.action_logout -> {
                    // 执行退出登录操作
                    logout()
                    return true
                }

                R.id.action_exit -> {
                    // 执行退出应用操作
                    exitApp()
                    return true
                }

                R.id.action_about -> {
                    // 跳转到关于界面
                    startActivity(Intent(this@MainActivity, AboutActivity::class.java))
                    return true
                }
            }
            // 未处理的菜单项返回false
            return false
        }
    }

    /**
     * 定时器监听器，处理定时器的计时和结束事件
     */
    private val onTimerListener = object : QuitTimer.OnTimerListener {
        /**
         * 定时器计时回调，每秒调用一次
         * @param remain 剩余时间（毫秒）
         */
        override fun onTick(remain: Long) {
            // 获取定时器菜单项
            if (timerItem == null) {
                timerItem = viewBinding.navigationView.menu.findItem(R.id.action_timer)
            }
            val title = getString(R.string.menu_timer)
            // 更新定时器菜单项标题，显示剩余时间
            timerItem?.title = if (remain == 0L) {
                title
            } else {
                TimeUtils.formatTime("$title(mm:ss)", remain)
            }
        }

        /**
         * 定时器结束时的回调
         * 定时器结束后自动退出应用
         */
        override fun onTimeEnd() {
            exitApp()
        }
    }

    /**
     * 显示定时器设置对话框
     * 用户可以选择预设的定时时间
     */
    private fun timerDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.menu_timer)
            .setItems(resources.getStringArray(R.array.timer_text)) { dialog: DialogInterface?, which: Int ->
                // 获取预设的定时时间数组
                val times = resources.getIntArray(R.array.timer_int)
                // 启动定时器
                startTimer(times[which])
            }
            .show()
    }

    /**
     * 启动定时器
     * @param minute 定时时间（分钟），0表示取消定时器
     */
    private fun startTimer(minute: Int) {
        // 启动或取消定时器
        quitTimer.start((minute * 60 * 1000).toLong())
        // 显示提示信息
        if (minute > 0) {
            toast(getString(R.string.timer_set, minute.toString()))
        } else {
            toast(R.string.timer_cancel)
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

    /**
     * 获取底部标签页的菜单项
     * 创建并配置标签页的图标和文本
     * @param icon 标签图标资源ID
     * @param text 标签文本
     * @return 标签页的视图绑定对象
     */
    private fun getTabItem(@DrawableRes icon: Int, text: CharSequence): TabItemBinding {
        // 填充标签项布局
        val binding = TabItemBinding.inflate(layoutInflater, viewBinding.tabBar, true)
        // 设置标签图标
        binding.ivIcon.setImageResource(icon)
        // 设置标签文本
        binding.tvTitle.text = text
        // 返回标签项的视图绑定
        return binding
    }

    /**
     * Activity销毁时调用的方法
     * 释放资源，停止定时器等
     */
    override fun onDestroy() {
        super.onDestroy()
        // 停止退出定时器
        quitTimer.stop()
    }
}