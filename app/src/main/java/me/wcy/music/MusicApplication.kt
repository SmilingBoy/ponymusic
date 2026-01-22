package me.wcy.music

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.content.res.Resources
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.blankj.utilcode.util.ActivityUtils
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.HiltAndroidApp
import me.wcy.music.account.service.UserService
import me.wcy.music.appwidget.WidgetRepository
import me.wcy.music.common.DarkModeService
import me.wcy.music.common.MusicFragmentContainerActivity
import me.wcy.music.service.MusicService
import me.wcy.music.service.PlayServiceModule
import me.wcy.music.service.likesong.LikeSongProcessor
import me.wcy.router.CRouter
import me.wcy.router.RouterClient
import top.wangchenyan.common.CommonApp
import top.wangchenyan.common.ext.findActivity
import java.lang.reflect.Field
import javax.inject.Inject

/**
 * 自定义Application
 * Created by wcy on 2015/11/27.
 */
@HiltAndroidApp
class MusicApplication : Application() {
    @Inject
    lateinit var userService: UserService

    @Inject
    lateinit var darkModeService: DarkModeService

    @Inject
    lateinit var likeSongProcessor: LikeSongProcessor

    override fun onCreate() {
        super.onCreate()

        CommonApp.init {
            test = BuildConfig.DEBUG
            isDarkMode = { darkModeService.isDarkMode() }
            titleLayoutConfig {
                isStatusBarDarkFontWhenAuto = { darkModeService.isDarkMode().not() }
                textColorAuto = { R.color.common_text_h1_color }
                textColorBlack = { R.color.common_text_h1_color }
                isTitleCenter = false
            }
            imageLoaderConfig {
                placeholderAvatar = R.drawable.ic_launcher_round
            }
            apiConfig({}) {
                codeJsonNames = listOf("code")
                msgJsonNames = listOf("message", "msg")
                dataJsonNames = listOf("data", "result")
                successCode = 200
            }
        }
        initCRouter()
        darkModeService.init()
        likeSongProcessor.init()

        val sessionToken =
            SessionToken(this, ComponentName(this, MusicService::class.java))
        val mediaControllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
        mediaControllerFuture.addListener({
            val player = mediaControllerFuture.get()
            PlayServiceModule.setPlayer(player)
            WidgetRepository.init(this)
        }, MoreExecutors.directExecutor())

        setGlobalFont()
    }

    private fun setGlobalFont() {
        try {
            // 兼容所有版本：从res/font目录加载字体（R.font.mr）
            val customFont: Typeface? = ResourcesCompat.getFont(this, R.font.mr)
            // 判空：避免字体加载失败导致后续反射出错
            customFont ?: return

            // 反射替换系统默认的MONOSPACE字体
            val field: Field = Typeface::class.java.getDeclaredField("MONOSPACE")
            field.isAccessible = true // 突破私有字段访问限制
            field.set(null, customFont) // 为静态字段赋值
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: Resources.NotFoundException) {
            // 捕获字体资源不存在的异常（比如R.font.mr写错/文件缺失）
            e.printStackTrace()
            println("字体资源加载失败：请检查res/font目录下是否有mr.ttf/mr.otf文件")
        } catch (e: Exception) {
            // 兜底捕获所有异常，避免崩溃
            e.printStackTrace()
        }
    }

    private fun initCRouter() {
        CRouter.setRouterClient(
            RouterClient.Builder()
                .baseUrl("app://music")
                .loginProvider { context, callback ->
                    var activity = context.findActivity()
                    if (activity == null) {
                        activity = ActivityUtils.getTopActivity()
                    }
                    if (activity != null) {
                        userService.checkLogin(activity) {
                            callback()
                        }
                    }
                }
                .fragmentContainerIntentProvider {
                    Intent(it, MusicFragmentContainerActivity::class.java)
                }
                .build()
        )
    }
}