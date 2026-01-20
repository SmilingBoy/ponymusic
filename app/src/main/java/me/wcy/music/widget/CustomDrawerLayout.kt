package me.wcy.music.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout


/**
 * 自定义DrawerLayout，限定仅左侧30dp内可触发抽屉滑动
 */
class CustomDrawerLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : DrawerLayout(context, attrs, defStyleAttr) {

    // 触发抽屉的左侧区域宽度（30dp，转成px）
    private val triggerWidth = dp2px(130f)


    /**
     * dp转px，适配不同屏幕密度
     */
    private fun dp2px(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }

    /**
     * 重写触摸事件拦截方法：仅左侧130dp内的滑动才触发抽屉
     */
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {

        if (ev.action == MotionEvent.ACTION_DOWN) {
            // 抽屉关闭时，限制触发区


            // 抽屉关闭时，只允许左侧 80dp 触发
            if (!isDrawerOpen(GravityCompat.START)) {
                val x = ev.getX()

                if (x <= triggerWidth) {
                    // ⭐ 关键：抢先拦截 DOWN
                    return true
                } else {
                    return false
                }
            }
        }
        // 满足条件：调用父类方法，触发抽屉滑动
        return super.onInterceptTouchEvent(ev)
    }
}