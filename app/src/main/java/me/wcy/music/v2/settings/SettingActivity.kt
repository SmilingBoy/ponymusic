package me.wcy.music.v2.settings

import android.os.Bundle
import com.blankj.utilcode.util.FragmentUtils
import dagger.hilt.android.AndroidEntryPoint
import me.wcy.music.R
import me.wcy.music.common.BaseMusicActivity
import me.wcy.music.databinding.ActivitySettingsBinding
import top.wangchenyan.common.ext.viewBindings

@AndroidEntryPoint
class SettingActivity : BaseMusicActivity() {

    private val binding by viewBindings<ActivitySettingsBinding>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)


        //显示 SettingsFragment
        val fragment = SettingsFragment()
        FragmentUtils.replace(supportFragmentManager, fragment, R.id.fragment_container)


    }


}