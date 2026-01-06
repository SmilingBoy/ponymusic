package me.wcy.music.main.playing

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import me.wcy.music.R
import me.wcy.music.common.BaseMusicActivity

@AndroidEntryPoint
class PlayingV2Activity : BaseMusicActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playing_v2)
    }
}