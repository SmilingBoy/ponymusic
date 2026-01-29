package me.wcy.music.v2.settings

import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import dagger.hilt.android.AndroidEntryPoint
import me.wcy.music.common.BaseMusicFragment
import me.wcy.music.databinding.FragmentSettingsBinding
import top.wangchenyan.common.ext.viewBindings

@AndroidEntryPoint
class SettingsFragment : BaseMusicFragment() {

    private val binding by viewBindings<FragmentSettingsBinding>()

    override fun getRootView(): View {
        return binding.root
    }

    override fun onLazyCreate() {
        super.onLazyCreate()


        initEvent()

    }

    private fun initEvent() {
        binding.serverUrls.setOnClickListener {

            ActivityUtils.startActivity(MusicServiceActivity::class.java)

        }
    }
}