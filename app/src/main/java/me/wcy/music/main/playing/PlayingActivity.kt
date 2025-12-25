package me.wcy.music.main.playing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.MediaItem
import com.blankj.utilcode.util.LogUtils
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.blurry.Blurry
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.wcy.lrcview.LrcView
import me.wcy.music.R
import me.wcy.music.common.BaseMusicActivity
import me.wcy.music.consts.RoutePath
import me.wcy.music.databinding.ActivityPlayingBinding
import me.wcy.music.discover.DiscoverApi
import me.wcy.music.ext.registerReceiverCompat
import me.wcy.music.main.playlist.CurrentPlaylistFragment
import me.wcy.music.service.PlayMode
import me.wcy.music.service.PlayState
import me.wcy.music.service.PlayerController
import me.wcy.music.service.likesong.LikeSongProcessor
import me.wcy.music.storage.LrcCache
import me.wcy.music.storage.preference.ConfigPreferences
import me.wcy.music.utils.BitmapUtils.transAlpha
import me.wcy.music.utils.LrcUtil
import me.wcy.music.utils.TimeUtils
import me.wcy.music.utils.getDuration
import me.wcy.music.utils.getLargeCover
import me.wcy.music.utils.getNmSongId
import me.wcy.music.utils.getSongId
import me.wcy.music.utils.isLocal
import me.wcy.router.annotation.Route
import top.wangchenyan.common.ext.toast
import top.wangchenyan.common.ext.viewBindings
import top.wangchenyan.common.net.apiCall
import top.wangchenyan.common.utils.LaunchUtils
import top.wangchenyan.common.utils.image.ImageUtils
import java.io.File
import javax.inject.Inject
import kotlin.math.abs

/**
 * 音乐播放界面
 * 显示当前播放歌曲的封面、歌词、播放控制等信息
 * 处理音乐播放状态的更新、进度控制、音量调节等功能
 * Created by wangchenyan.top on 2023/9/4.
 */
@Route(RoutePath.PLAYING) // 路由注解，指定页面路径
@AndroidEntryPoint // Hilt依赖注入注解
class PlayingActivity : BaseMusicActivity() {
    // 视图绑定
    private val viewBinding by viewBindings<ActivityPlayingBinding>()

    // 播放器控制器，用于控制音乐播放
    @Inject
    lateinit var playerController: PlayerController

    // 喜欢歌曲处理器，用于处理歌曲收藏功能
    @Inject
    lateinit var likeSongProcessor: LikeSongProcessor

    // 音频管理器，用于控制系统音量
    private val audioManager by lazy {
        getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    // 默认封面图片
    private val defaultCoverBitmap by lazy {
        BitmapFactory.decodeResource(resources, R.drawable.bg_playing_default_cover)
    }

    // 默认背景图片
    private val defaultBgBitmap by lazy {
        BitmapFactory.decodeResource(
            resources,
            R.drawable.bg_playing_default,
            BitmapFactory.Options().apply {
                inPreferredConfig = Bitmap.Config.RGB_565
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    outConfig = Bitmap.Config.RGB_565
                }
            }
        )
    }

    // 加载歌词的协程任务
    private var loadLrcJob: Job? = null

    // 上次更新的进度时间戳（毫秒）
    private var lastProgress = 0

    // 是否正在拖动进度条
    private var isDraggingProgress = false

    /**
     * 生命周期方法：Activity创建时调用
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        initWindowInsets() // 初始化窗口内嵌边距
        initTitle() // 初始化标题栏
        initVolume() // 初始化音量控制
        initCover() // 初始化封面
        initLrc() // 初始化歌词
        initActions() // 初始化操作按钮
        initPlayControl() // 初始化播放控制
        initData() // 初始化数据
        switchCoverLrc(true) // 默认显示封面
    }

    /**
     * 初始化窗口内嵌边距
     */
    private fun initWindowInsets() {
        configWindowInsets {
            fillNavBar = false // 不填充导航栏
            fillDisplayCutout = false // 不填充刘海屏
            statusBarTextDarkStyle = false // 状态栏文字使用浅色
            navBarButtonDarkStyle = false // 导航栏按钮使用浅色
        }

        // 更新内嵌边距的函数
        val updateInsets = { insets: WindowInsetsCompat ->
            // 获取状态栏、导航栏和刘海屏的内嵌边距
            val result = insets.getInsets(
                WindowInsetsCompat.Type.statusBars()
                        or WindowInsetsCompat.Type.navigationBars()
                        or WindowInsetsCompat.Type.displayCutout()
            )
            // 更新内容区域的内边距
            viewBinding.llContent.updatePadding(
                left = result.left,
                top = result.top,
                right = result.right,
                bottom = result.bottom,
            )
        }
        // 获取当前窗口的内嵌边距
        val insets = ViewCompat.getRootWindowInsets(viewBinding.llContent)
        if (insets != null) {
            updateInsets(insets)
        }
        // 设置内嵌边距变化监听器
        ViewCompat.setOnApplyWindowInsetsListener(viewBinding.llContent) { v, insets ->
            updateInsets(insets)
            insets
        }
    }

    /**
     * 初始化标题栏
     */
    private fun initTitle() {
        // 设置关闭按钮点击事件，返回上一页
        viewBinding.titleLayout.ivClose.setOnClickListener {
            onBackPressed()
        }
    }

    /**
     * 初始化音量控制
     */
    private fun initVolume() {
        // 设置音量进度条的最大值为系统音乐音量的最大值
        viewBinding.volumeLayout.sbVolume.max =
            audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        // 设置音量进度条的当前值为系统音乐音量的当前值
        viewBinding.volumeLayout.sbVolume.progress =
            audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        // 创建音量变化广播过滤器
        val filter = IntentFilter(VOLUME_CHANGED_ACTION)
        // 注册音量变化广播接收器
        registerReceiverCompat(volumeReceiver, filter)
    }

    /**
     * 初始化封面
     */
    private fun initCover() {
        // 获取当前播放状态
        val playState = playerController.playState.value
        // 初始化唱针状态
        viewBinding.albumCoverView.initNeedle(playState.isPlaying)
        // 设置封面点击事件，切换到歌词显示
        viewBinding.albumCoverView.setOnClickListener {
            switchCoverLrc(false)
        }
        // 设置默认封面
        setDefaultCover()
    }

    /**
     * 初始化歌词
     */
    private fun initLrc() {
        // 设置歌词视图可拖动
        viewBinding.lrcView.setDraggable(true) { view, time ->
            // 获取当前播放状态
            val playState = playerController.playState.value
            // 如果正在播放或暂停状态
            if (playState.isPlaying || playState.isPausing) {
                // 跳转到指定时间
                playerController.seekTo(time.toInt())
                // 如果是暂停状态，自动播放
                if (playState.isPausing) {
                    playerController.playPause()
                }
                return@setDraggable true
            }
            return@setDraggable false
        }
        // 设置歌词视图点击事件，切换到封面显示
        viewBinding.lrcView.setOnTapListener { view: LrcView?, x: Float, y: Float ->
            switchCoverLrc(true)
        }
    }

    /**
     * 初始化操作按钮
     */
    private fun initActions() {
        // 设置喜欢按钮点击事件
        viewBinding.controlLayout.ivLike.setOnClickListener {
            lifecycleScope.launch {
                // 获取当前播放歌曲
                val song = playerController.currentSong.value ?: return@launch
                // 执行喜欢/取消喜欢操作
                val res = likeSongProcessor.like(this@PlayingActivity, song.getSongId())
                if (res.isSuccess()) {
                    // 更新操作按钮状态
                    updateOnlineActionsState(song)
                } else {
                    // 显示错误提示
                    toast(res.msg)
                }
            }
        }
        // 设置下载按钮点击事件
        viewBinding.controlLayout.ivDownload.setOnClickListener {
            lifecycleScope.launch {
                // 获取当前播放歌曲
                val song = playerController.currentSong.value ?: return@launch
                // 调用API获取歌曲下载链接
                val res = apiCall {
                    DiscoverApi.get()
                        .getSongUrl(song.getSongId(), ConfigPreferences.downloadSoundQuality)
                }
                if (res.isSuccessWithData() && res.getDataOrThrow().isNotEmpty()) {
                    // 获取下载链接
                    val url = res.getDataOrThrow().first().url
                    // 调用浏览器打开下载链接
                    LaunchUtils.launchBrowser(this@PlayingActivity, url)
                } else {
                    // 显示错误提示
                    toast(res.msg)
                }
            }
        }
    }

    /**
     * 初始化播放控制
     */
    private fun initPlayControl() {
        // 监听播放模式变化
        lifecycleScope.launch {
            playerController.playMode.collectLatest { playMode ->
                // 更新播放模式图标
                viewBinding.controlLayout.ivMode.setImageLevel(playMode.value)
            }
        }

        // 设置播放模式按钮点击事件
        viewBinding.controlLayout.ivMode.setOnClickListener {
            switchPlayMode()
        }
        // 设置播放/暂停按钮点击事件
        viewBinding.controlLayout.flPlay.setOnClickListener {
            playerController.playPause()
        }
        // 设置上一首按钮点击事件
        viewBinding.controlLayout.ivPrev.setOnClickListener {
            playerController.prev()
        }
        // 设置下一首按钮点击事件
        viewBinding.controlLayout.ivNext.setOnClickListener {
            playerController.next()
        }
        // 设置播放列表按钮点击事件
        viewBinding.controlLayout.ivPlaylist.setOnClickListener {
            // 显示当前播放列表弹窗
            CurrentPlaylistFragment.newInstance()
                .show(supportFragmentManager, CurrentPlaylistFragment.TAG)
        }
        // 设置进度条监听
        viewBinding.controlLayout.sbProgress.setOnSeekBarChangeListener(object :
            OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // 避免过于频繁更新时间显示，每1秒更新一次
                if (abs(progress - lastProgress) >= DateUtils.SECOND_IN_MILLIS) {
                    // 更新当前播放时间
                    viewBinding.controlLayout.tvCurrentTime.text =
                        TimeUtils.formatMs(progress.toLong())
                    // 记录上次更新的进度
                    lastProgress = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // 开始拖动进度条
                isDraggingProgress = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar ?: return
                // 停止拖动进度条
                isDraggingProgress = false
                // 获取当前播放状态
                val playState = playerController.playState.value
                // 如果正在播放或暂停状态
                if (playState.isPlaying || playState.isPausing) {
                    // 获取拖动后的进度
                    val progress = seekBar.progress
                    // 跳转到指定进度
                    playerController.seekTo(progress)
                    // 如果有歌词，更新歌词时间
                    if (viewBinding.lrcView.hasLrc()) {
                        viewBinding.lrcView.updateTime(progress.toLong())
                    }
                } else {
                    // 重置进度条
                    seekBar.progress = 0
                }
            }
        })
        // 设置音量进度条监听
        viewBinding.volumeLayout.sbVolume.setOnSeekBarChangeListener(object :
            OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // 进度变化时不做处理
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // 开始拖动时不做处理
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar ?: return
                // 设置系统音量
                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    seekBar.progress,
                    AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE
                )
            }
        })
    }

    /**
     * 初始化数据
     */
    private fun initData() {
        // 歌曲更新回调函数
        val onSongUpdate: (MediaItem) -> Unit = { song ->
            // 更新歌曲标题
            viewBinding.controlLayout.tvTitle.text = song.mediaMetadata.title
            // 更新歌手名称
            viewBinding.controlLayout.tvArtist.text = song.mediaMetadata.artist
            // 设置进度条最大值
            viewBinding.controlLayout.sbProgress.max = song.mediaMetadata.getDuration().toInt()
            // 设置当前进度
            viewBinding.controlLayout.sbProgress.progress =
                playerController.playProgress.value.toInt()
            // 重置缓冲进度
            viewBinding.controlLayout.sbProgress.secondaryProgress = 0
            // 更新当前播放时间
            viewBinding.controlLayout.tvCurrentTime.text =
                TimeUtils.formatMs(playerController.playProgress.value)
            // 更新总时长
            viewBinding.controlLayout.tvTotalTime.text =
                TimeUtils.formatMs(song.mediaMetadata.getDuration())
            // 更新封面
            updateCover(song)
            // 更新歌词
            updateLrc(song)
            // 重置封面视图
            viewBinding.albumCoverView.reset()
            // 更新播放状态
            updatePlayState(playerController.playState.value)
            // 更新在线操作按钮状态
            updateOnlineActionsState(song)
        }
        // 监听当前播放歌曲变化
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                playerController.currentSong.collectLatest { song ->
                    if (song != null) {
                        LogUtils.d(" songId ${song.getNmSongId()}")
                        // 更新歌曲信息
                        onSongUpdate(song)
                    } else {
                        // 没有歌曲播放时，关闭当前页面
                        finish()
                    }
                }
            }
        }

        // 监听播放状态变化
        lifecycleScope.launch {
            playerController.playState.collectLatest { playState ->
                updatePlayState(playState)
            }
        }

        // 监听播放进度变化
        lifecycleScope.launch {
            playerController.playProgress.collectLatest { progress ->
                // 如果不是用户拖动进度条
                if (isDraggingProgress.not()) {
                    // 更新进度条
                    viewBinding.controlLayout.sbProgress.progress = progress.toInt()
                }
                // 如果有歌词，更新歌词时间
                if (viewBinding.lrcView.hasLrc()) {
                    viewBinding.lrcView.updateTime(progress)
                }
            }
        }

        // 监听缓冲进度变化
        lifecycleScope.launch {
            playerController.bufferingPercent.collectLatest { percent ->
                // 更新缓冲进度条
                viewBinding.controlLayout.sbProgress.secondaryProgress =
                    viewBinding.controlLayout.sbProgress.max * percent / 100
            }
        }
    }

    /**
     * 更新封面
     * @param song 歌曲信息
     */
    private fun updateCover(song: MediaItem) {
        // 先设置默认封面
        setDefaultCover()
        // 加载歌曲封面
        ImageUtils.loadBitmap(song.getLargeCover()) {
            if (it.isSuccessWithData()) {
                // 获取封面图片
                val bitmap = it.getDataOrThrow()
                // 设置封面图片
                viewBinding.albumCoverView.setCoverBitmap(bitmap)
                // 设置背景模糊效果
                Blurry.with(this).sampling(10).from(bitmap).into(viewBinding.ivPlayingBg)
                // 更新歌词遮罩
                updateLrcMask()
            }
        }
    }

    /**
     * 设置默认封面
     */
    private fun setDefaultCover() {
        // 设置默认封面图片
        viewBinding.albumCoverView.setCoverBitmap(defaultCoverBitmap)
        // 设置默认背景图片
        viewBinding.ivPlayingBg.setImageBitmap(defaultBgBitmap)
        // 更新歌词遮罩
        updateLrcMask()
    }

    /**
     * 更新歌词遮罩
     */
    private fun updateLrcMask() {
        // 更新顶部遮罩
        updateLrcMask(viewBinding.ivLrcTopMask, true)
        // 更新底部遮罩
        updateLrcMask(viewBinding.ivLrcBottomMask, false)
    }

    /**
     * 更新歌词遮罩
     * @param maskView 遮罩视图
     * @param topToBottom 是否从上到下渐变
     */
    private fun updateLrcMask(maskView: ImageView, topToBottom: Boolean) {
        maskView.doOnLayout {
            // 将背景视图转换为位图
            val bitmap = com.blankj.utilcode.util.ImageUtils.view2Bitmap(viewBinding.flBackground)
            // 获取遮罩视图在窗口中的位置
            val location = IntArray(2)
            maskView.getLocationInWindow(location)
            // 裁剪背景位图，只保留遮罩视图位置的部分
            val clippedBitmap = com.blankj.utilcode.util.ImageUtils.clip(
                bitmap,
                location[0],
                location[1],
                maskView.width,
                maskView.height,
                true
            )
            // 根据方向添加透明度渐变
            val alphaBitmap = clippedBitmap.transAlpha(topToBottom)
            // 回收裁剪后的位图
            clippedBitmap.recycle()
            // 设置遮罩图片
            maskView.setImageBitmap(alphaBitmap)
        }
    }

    /**
     * 更新歌词
     * @param song 歌曲信息
     */
    private fun updateLrc(song: MediaItem) {
        // 取消之前的歌词加载任务
        loadLrcJob?.cancel()
        loadLrcJob = null
        // 尝试从缓存获取歌词文件路径
        val lrcPath = LrcCache.getLrcFilePath(song)
        if (lrcPath?.isNotEmpty() == true) {
            // 如果缓存存在，加载缓存的歌词
            loadLrc(lrcPath)
            return
        }
        // 清空歌词视图
        viewBinding.lrcView.loadLrc("")
        // 如果是本地歌曲
        if (song.isLocal()) {
            // 显示暂无歌词
            setLrcLabel("暂无歌词")
        } else {
            // 显示加载中提示
            setLrcLabel("歌词加载中…")
            // 启动协程加载歌词
            loadLrcJob = lifecycleScope.launch {
                kotlin.runCatching {
                    // 调用API获取歌词
                    val lrcWrap = DiscoverApi.get().getAlbumSongDetail(albumId = song.getNmSongId())
                    // 检查歌词是否有效
                    val lrcWrapD = lrcWrap.firstOrNull()
                    if (lrcWrapD == null) {
                        throw IllegalStateException("lrcWrapD is null")
                    }

                    val lrc = lrcWrapD.lyrics.ifEmpty {
                        throw IllegalStateException("lrc is invalid")
                    }
                    val parseLrc = LrcUtil.parseLrc(lrc)
                    if (parseLrc.isEmpty()){
                        throw IllegalStateException("parseLrc is invalid")
                    }
                    parseLrc
                }.onSuccess {
                    // 保存歌词到缓存
                    val file = LrcCache.saveLrcFile(song, it)
                    LogUtils.d("保存的歌词文件 $file")
                    // 加载保存的歌词
                    loadLrc(file.path)
                }.onFailure {
                    // 打印错误日志
                    Log.e(TAG, "load lrc error", it)
                    // 显示加载失败提示
                    setLrcLabel("歌词加载失败")
                }
            }
        }
    }

    /**
     * 加载歌词文件
     * @param path 歌词文件路径
     */
    private fun loadLrc(path: String) {
        val file = File(path)
        viewBinding.lrcView.loadLrc(file)
    }

    /**
     * 设置歌词标签
     * @param label 要显示的标签文本
     */
    private fun setLrcLabel(label: String) {
        viewBinding.lrcView.setLabel(label)
    }

    /**
     * 切换封面和歌词显示
     * @param showCover 是否显示封面，true显示封面，false显示歌词
     */
    private fun switchCoverLrc(showCover: Boolean) {
        // 非横屏模式下才切换显示
        if (resources.configuration.orientation != Configuration.ORIENTATION_LANDSCAPE) {
            viewBinding.albumCoverView.isVisible = showCover
            viewBinding.lrcLayout.isVisible = showCover.not()
        }
    }

    /**
     * 切换播放模式
     * 循环模式：Loop -> Shuffle -> Single -> Loop
     */
    private fun switchPlayMode() {
        val mode = when (playerController.playMode.value) {
            PlayMode.Loop -> PlayMode.Shuffle
            PlayMode.Shuffle -> PlayMode.Single
            PlayMode.Single -> PlayMode.Loop
        }
        // 显示切换提示
        toast(mode.nameRes)
        // 更新播放模式
        playerController.setPlayMode(mode)
    }

    /**
     * 更新播放状态
     * @param playState 播放状态
     */
    private fun updatePlayState(playState: PlayState) {
        when (playState) {
            // 准备中状态
            PlayState.Preparing -> {
                viewBinding.controlLayout.flPlay.isEnabled = false
                viewBinding.controlLayout.ivPlay.isSelected = false
                viewBinding.controlLayout.loadingProgress.isVisible = true
                viewBinding.albumCoverView.pause()
            }

            // 播放中状态
            PlayState.Playing -> {
                viewBinding.controlLayout.flPlay.isEnabled = true
                viewBinding.controlLayout.ivPlay.isSelected = true
                viewBinding.controlLayout.loadingProgress.isVisible = false
                viewBinding.albumCoverView.start()
            }

            // 其他状态（暂停、停止等）
            else -> {
                viewBinding.controlLayout.flPlay.isEnabled = true
                viewBinding.controlLayout.ivPlay.isSelected = false
                viewBinding.controlLayout.loadingProgress.isVisible = false
                viewBinding.albumCoverView.pause()
            }
        }
    }

    /**
     * 更新在线操作按钮状态
     * @param song 歌曲信息
     */
    private fun updateOnlineActionsState(song: MediaItem) {
        // 只有在线歌曲才显示操作按钮
        viewBinding.controlLayout.llActions.isVisible = song.isLocal().not()
        // 更新喜欢按钮状态
        viewBinding.controlLayout.ivLike.isSelected = likeSongProcessor.isLiked(song.getSongId())
    }

    /**
     * 生命周期方法：Activity销毁时调用
     * 释放资源和取消注册接收器
     */
    override fun onDestroy() {
        super.onDestroy()
        // 取消注册音量变化广播接收器
        unregisterReceiver(volumeReceiver)
        // 回收默认封面位图
        defaultCoverBitmap.recycle()
        // 回收默认背景位图
        defaultBgBitmap.recycle()
    }

    /**
     * 音量变化广播接收器
     * 当系统音量变化时更新UI
     */
    private val volumeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // 更新音量进度条
            viewBinding.volumeLayout.sbVolume.progress =
                audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        }
    }

    companion object {
        private const val TAG = "PlayingActivity" // 日志标签
        private const val VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION" // 音量变化广播动作
    }
}