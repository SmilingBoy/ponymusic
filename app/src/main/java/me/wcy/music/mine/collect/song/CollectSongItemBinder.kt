package me.wcy.music.mine.collect.song

import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SizeUtils
import me.wcy.music.databinding.ItemCollectSongBinding
import me.wcy.music.discover.playlist.detail.bean.NmSongData
import me.wcy.music.utils.ImageUtils.loadCover
import me.wcy.music.utils.MusicUtils
import me.wcy.music.utils.NavidromeUtil
import me.wcy.radapter3.RItemBinder

/**
 * 收藏歌曲项Binder
 */
class CollectSongItemBinder(
    private val listener: OnItemClickListener
) : RItemBinder<ItemCollectSongBinding, NmSongData>() {

    interface OnItemClickListener {
        fun onItemClick(item: NmSongData, position: Int)
        fun onMoreClick(item: NmSongData, position: Int)
    }

    override fun onBind(viewBinding: ItemCollectSongBinding, item: NmSongData, position: Int) {


        viewBinding.root.setOnClickListener {
            listener.onItemClick(item, position)
        }
        viewBinding.ivMore.setOnClickListener {
            listener.onMoreClick(item, position)
        }
        viewBinding.ivCover.loadCover(NavidromeUtil.getCover(item.id), SizeUtils.dp2px(4f))
        viewBinding.tvTitle.text = item.title
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