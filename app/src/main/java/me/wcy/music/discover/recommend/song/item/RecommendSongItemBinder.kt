package me.wcy.music.discover.recommend.song.item

import com.blankj.utilcode.util.SizeUtils
import me.wcy.music.common.OnItemClickListener2
import me.wcy.music.databinding.ItemRecommendSongBinding
import me.wcy.music.discover.playlist.detail.bean.NmAlbumData
import me.wcy.music.utils.ImageUtils.loadCover
import me.wcy.music.utils.NavidromeUtil
import me.wcy.radapter3.RItemBinder

/**
 * Created by wangchenyan.top on 2023/9/15.
 */
class RecommendSongItemBinder(private val listener: OnItemClickListener2<NmAlbumData>) :
    RItemBinder<ItemRecommendSongBinding, NmAlbumData>() {

    override fun onBind(viewBinding: ItemRecommendSongBinding, item: NmAlbumData, position: Int) {
        viewBinding.root.setOnClickListener {
            listener.onItemClick(item, position)
        }
        viewBinding.ivMore.setOnClickListener {
            listener.onMoreClick(item, position)
        }
        viewBinding.ivCover.loadCover(NavidromeUtil.getCover(item.id), SizeUtils.dp2px(4f))
        viewBinding.tvTitle.text = item.name
//        viewBinding.tvTag.isVisible = item.recommendReason.isNotEmpty()
//        viewBinding.tvTag.text = item.recommendReason
        viewBinding.tvSubTitle.text = buildString {
            append(item.artist)
//            append(" - ")
//            append(item.album)
//            item.originSongSimpleData?.let { originSong ->
//                append(" | 原唱: ")
//                append(originSong.artists.joinToString("/") { it.name })
//            }
        }
    }
}
