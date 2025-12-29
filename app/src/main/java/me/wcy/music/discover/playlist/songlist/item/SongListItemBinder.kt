package me.wcy.music.discover.playlist.songlist.item

import com.blankj.utilcode.util.SizeUtils
import me.wcy.music.databinding.ItemSonglistSongBinding
import me.wcy.music.discover.playlist.detail.bean.NmSongData
import me.wcy.music.utils.ImageUtils.loadCover
import me.wcy.music.utils.NavidromeUtil
import me.wcy.radapter3.RItemBinder

/**
 * 歌曲列表项Binder
 */
class SongListItemBinder(private val listener: OnItemClickListener) :
    RItemBinder<ItemSonglistSongBinding, NmSongData>() {

    interface OnItemClickListener {
        fun onItemClick(item: NmSongData)
        fun onMoreClick(item: NmSongData)
    }

    override fun onBind(viewBinding: ItemSonglistSongBinding, item: NmSongData, position: Int) {
        viewBinding.root.setOnClickListener {
            listener.onItemClick(item)
        }
        viewBinding.ivMore.setOnClickListener {
            listener.onMoreClick(item)
        }
        viewBinding.ivCover.loadCover(NavidromeUtil.getCover(item.id), SizeUtils.dp2px(4f))
        viewBinding.tvTitle.text = item.title.split("/").last()
//        viewBinding.tvTag.isVisible = item.recommendReason.isNotEmpty()
//        viewBinding.tvTag.text = item.recommendReason
        viewBinding.tvSubTitle.text = buildString {
            append(item.artist)
            append(" - ")
            append(item.album)
//            item.originSongSimpleData?.let { originSong ->
//                append(" | 原唱: ")
//                append(originSong.artists.joinToString("/") { it.name })
//            }
        }
    }
}