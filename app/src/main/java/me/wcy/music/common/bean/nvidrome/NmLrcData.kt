package me.wcy.music.common.bean.nvidrome

import com.google.gson.annotations.SerializedName

data class NmLrcData(
    @SerializedName("lang")
    var lang: String = "",
    @SerializedName("line")
    var line: List<Line> = listOf(),
    @SerializedName("synced")
    var synced: Boolean = false
)

data class Line(
    @SerializedName("start")
    var start: Long = 0L,
    @SerializedName("value")
    var value: String = ""
)


