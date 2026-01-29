package me.wcy.music.v2.settings

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import me.wcy.music.databinding.ItemMusicServiceBinding
import me.wcy.music.storage.db.entity.MusicServiceEntity

class MusicServiceAdapter :
    BaseQuickAdapter<MusicServiceEntity, MusicServiceAdapter.ItemVH>() {

    override fun onBindViewHolder(
        holder: ItemVH,
        position: Int,
        item: MusicServiceEntity?
    ) {
        item?.let {
            holder.binding.apply {
                tvName.text = it.name
//                tvUrl.text = it.wanUrl
//                ivDefault.isVisible = it.isDefault
            }
        }
    }

    class ItemVH(val binding: ItemMusicServiceBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): ItemVH {
        return ItemVH(ItemMusicServiceBinding.inflate(LayoutInflater.from(context), parent, false))
    }

}