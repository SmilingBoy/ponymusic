package me.wcy.music.utils

import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.TimeUtils
import me.wcy.music.common.bean.nvidrome.NmLrcData

object LrcUtil {

    fun parseLrc(lrcJsonStr: String): String {

        val listType = GsonUtils.getListType(NmLrcData::class.java);

        val lrcDataList: List<NmLrcData> = GsonUtils.fromJson(lrcJsonStr, listType)

        val firstOrNull = lrcDataList.firstOrNull()
        if (firstOrNull == null) {
            return ""
        }

        val sb = StringBuilder()
        firstOrNull.line.forEach {

            //[00:05.80]把它发送到我的手机里，把它发送到我的手机里。
            val t = millisToMs(it.start)
            val l = "[$t]${it.value}"
            sb.append(l).append("\n")
        }

        return sb.toString()
    }

    /**
     * 毫秒转时分秒格式（HH:MM:SS.SS）
     * @param millis 要转换的毫秒数（≥0）
     * @return 格式化后的字符串，例如：
     *         1000 → 00:00:01.00
     *         61000 → 00:01:01.00
     *         3661000 → 01:01:01.00
     *         0 → 00:00:00.00
     */
    fun millisToHms(millis: Long): String {
        // 确保毫秒数非负
        val inputMillis = if (millis < 0) 0 else millis

        // 1. 拆分小时、分钟、秒、毫秒
        val totalSeconds = inputMillis / 1000 // 总秒数
        val remainingMillis = inputMillis % 1000 // 剩余毫秒（取后两位）
        val hours = (totalSeconds / 3600).toInt() // 小时
        val minutes = ((totalSeconds % 3600) / 60).toInt() // 分钟
        val seconds = (totalSeconds % 60).toInt() // 秒

        // 2. 格式化：补零到两位，毫秒保留两位（不足补零）
        val formattedHours = String.format("%02d", hours)
        val formattedMinutes = String.format("%02d", minutes)
        val formattedSeconds = String.format("%02d", seconds)
        val formattedMillis = String.format("%02d", remainingMillis / 10) // 毫秒取前两位（1000ms → 00）

        // 3. 拼接成 HH:MM:SS.SS 格式
        return "$formattedHours:$formattedMinutes:$formattedSeconds.$formattedMillis"
    }
    // 示例：只保留分秒（MM:SS.SS）
    fun millisToMs(millis: Long): String {
        val inputMillis = if (millis < 0) 0 else millis
        val totalSeconds = inputMillis / 1000
        val remainingMillis = inputMillis % 1000
        val minutes = ((totalSeconds % 3600) / 60).toInt()
        val seconds = (totalSeconds % 60).toInt()
        val formattedMinutes = String.format("%02d", minutes)
        val formattedSeconds = String.format("%02d", seconds)
        val formattedMillis = String.format("%02d", remainingMillis / 10)
        return "$formattedMinutes:$formattedSeconds.$formattedMillis"
    }
}