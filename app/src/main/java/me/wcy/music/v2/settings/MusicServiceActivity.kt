package me.wcy.music.v2.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.wcy.music.common.BaseMusicActivity
import me.wcy.music.databinding.ActivityMusicServiceBinding
import me.wcy.music.databinding.BottomSheetMusicServiceBinding
import me.wcy.music.storage.db.dao.MusicServiceDao
import me.wcy.music.storage.db.entity.MusicServiceEntity
import me.wcy.music.storage.preference.ConfigPreferences
import top.wangchenyan.common.ext.viewBindings
import javax.inject.Inject

@AndroidEntryPoint
class MusicServiceActivity : BaseMusicActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var adapter: MusicServiceAdapter

    private val binding by viewBindings<ActivityMusicServiceBinding>()

    @Inject
    lateinit var musicServiceDao: MusicServiceDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        recyclerView = binding.recyclerView
        fab = binding.fab

        adapter = MusicServiceAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fab.setOnClickListener {
            startActivity(Intent(this, AddMusicServiceActivity::class.java))
        }

        adapter.setOnItemClickListener { _, _, position ->
            showBottomSheet(adapter.items[position])
        }

        loadServices()
    }

    private fun loadServices() {
        CoroutineScope(Dispatchers.IO).launch {
            val services = musicServiceDao.getAll()
            runOnUiThread {
                adapter.submitList(services)
            }
        }
    }

    private fun showBottomSheet(service: MusicServiceEntity) {
        val context = this
        val dialog = BottomSheetDialog(context)

        val b = BottomSheetMusicServiceBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(b.root)

        b.btnDefault.setOnClickListener {
            setDefault(service, dialog)
        }

        b.btnModify.setOnClickListener {
            val intent = Intent(context, AddMusicServiceActivity::class.java)
            intent.putExtra("service", service)
            context.startActivity(intent)
            dialog.dismiss()
        }

        b.btnRemove.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                musicServiceDao.delete(service)
                runOnUiThread {
                    adapter.remove(service)
                    dialog.dismiss()
                }
            }
        }

        b.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    /**
     * 设为默认
     */
    private fun setDefault(
        service: MusicServiceEntity,
        dialog: BottomSheetDialog
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            musicServiceDao.resetDefault()
            musicServiceDao.setDefault(service.id)
            ConfigPreferences.apiDomain = service.wanUrl
            runOnUiThread {
                loadServices()
                dialog.dismiss()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        loadServices()
    }
}

