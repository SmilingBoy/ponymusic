package me.wcy.music.v2.album

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SizeUtils
import com.chad.library.adapter4.BaseQuickAdapter
import me.wcy.music.databinding.ItemAlbumV2Binding
import me.wcy.music.discover.playlist.detail.bean.NmAlbumData
import me.wcy.music.utils.ImageUtils.loadCover
import me.wcy.music.utils.NavidromeUtil

class AlbumAdapter : BaseQuickAdapter<NmAlbumData, AlbumAdapter.ItemVH>() {

    class ItemVH(val binding: ItemAlbumV2Binding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(
        holder: AlbumAdapter.ItemVH,
        position: Int,
        item: NmAlbumData?
    ) {

        holder.binding.apply {
            item?.let {

                ivCover.loadCover(NavidromeUtil.getCover(item.id), SizeUtils.dp2px(4f))

                tvName.text = item.name
                tvCount.text = "${item.songCount}首, by ${item.albumArtist}"

            }
        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): AlbumAdapter.ItemVH {
        return ItemVH(ItemAlbumV2Binding.inflate(LayoutInflater.from(context), parent, false))
    }


}