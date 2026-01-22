package me.wcy.music.v2.song

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SizeUtils
import com.chad.library.adapter4.BaseQuickAdapter
import me.wcy.music.databinding.ItemSongBinding
import me.wcy.music.discover.playlist.detail.bean.NmSongData
import me.wcy.music.utils.ImageUtils.loadCover
import me.wcy.music.utils.NavidromeUtil

class SongAdapter : BaseQuickAdapter<NmSongData, SongAdapter.ItemVH>() {

    class ItemVH(val binding: ItemSongBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(
        holder: ItemVH,
        position: Int,
        item: NmSongData?
    ) {
        holder.binding.apply {
            item?.let {
                ivCover.loadCover(NavidromeUtil.getCover(item.id), SizeUtils.dp2px(4f))
                tvTitle.text = item.title.split("/").last()
                tvSubTitle.text = buildString {
                    append(item.artist)
                    append(" - ")
                    append(item.album)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): ItemVH {
        return ItemVH(ItemSongBinding.inflate(LayoutInflater.from(context), parent, false))
    }
}