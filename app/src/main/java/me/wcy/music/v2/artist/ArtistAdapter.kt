package me.wcy.music.v2.artist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SizeUtils
import com.chad.library.adapter4.BaseQuickAdapter
import me.wcy.music.databinding.ItemArtistV2Binding
import me.wcy.music.discover.artist.bean.NmArtistData
import me.wcy.music.utils.ImageUtils.loadCover
import me.wcy.music.utils.NavidromeUtil

class ArtistAdapter : BaseQuickAdapter<NmArtistData, ArtistAdapter.ItemVH>() {

    class ItemVH(val binding: ItemArtistV2Binding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(
        holder: ItemVH,
        position: Int,
        item: NmArtistData?
    ) {
        holder.binding.apply {
            item?.let {
                // 加载歌手封面
                ivCover.loadCover(NavidromeUtil.getCover(item.id), SizeUtils.dp2px(4f))
                tvName.text = item.name
                tvCount.text = "${item.songCount}首歌曲, ${item.albumCount}张专辑"
            }
        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): ItemVH {
        return ItemVH(ItemArtistV2Binding.inflate(LayoutInflater.from(context), parent, false))
    }

}