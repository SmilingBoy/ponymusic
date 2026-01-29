package me.wcy.music.v2.settings

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.wcy.music.common.BaseMusicActivity
import me.wcy.music.databinding.ActivityAddMusicServiceBinding
import me.wcy.music.storage.db.dao.MusicServiceDao
import me.wcy.music.storage.db.entity.MusicServiceEntity
import me.wcy.music.storage.preference.ConfigPreferences
import top.wangchenyan.common.ext.viewBindings
import top.wangchenyan.common.utils.ToastUtils
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class AddMusicServiceActivity : BaseMusicActivity() {

    private val binding by viewBindings<ActivityAddMusicServiceBinding>()

    @Inject
    lateinit var musicServiceDao: MusicServiceDao

    private var editingService: MusicServiceEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        editingService = intent.getParcelableExtra("service") as? MusicServiceEntity
        if (editingService != null) {
            setTitle("编辑服务地址")
            fillForm(editingService!!)
        }

        binding.btnSave.setOnClickListener {
            saveService()
        }
    }

    private fun fillForm(service: MusicServiceEntity) {
        binding.etName.setText(service.name)
        binding.etWanUrl.setText(service.wanUrl)
        binding.etLanUrl.setText(service.lanUrl)
        binding.etUsername.setText(service.username)
        binding.etPassword.setText(service.password)
        binding.swDefault.isChecked = service.isDefault
    }

    private fun saveService() {
        var name = binding.etName.text.toString().trim()
        val wanUrl = binding.etWanUrl.text.toString().trim()
        val lanUrl = binding.etLanUrl.text.toString().trim()
        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val isDefault = binding.swDefault.isChecked

        if (name.isEmpty()) {
            name = "自定义服务" + Random(System.currentTimeMillis()).nextInt(0, 1000)
        }

        if (wanUrl.isEmpty() && lanUrl.isEmpty()) {
            ToastUtils.show("请填写完整")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            if (isDefault) {
                musicServiceDao.resetDefault()
            }

            val service = editingService?.copy(
                name = name,
                wanUrl = wanUrl,
                lanUrl = lanUrl,
                username = username,
                password = password,
                isDefault = isDefault
            ) ?: MusicServiceEntity(
                name = name,
                wanUrl = wanUrl,
                lanUrl = lanUrl,
                username = username,
                password = password,
                isDefault = isDefault
            )

            if (editingService != null) {
                musicServiceDao.delete(editingService!!)
            }
            musicServiceDao.insert(service)
            if (isDefault) {
                ConfigPreferences.apiDomain = wanUrl
            }

            runOnUiThread {
                finish()
            }
        }
    }

}