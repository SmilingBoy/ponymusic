package me.wcy.music.search.album

import androidx.core.view.isGone
import me.wcy.music.common.OnItemClickListener2
import me.wcy.music.databinding.ItemSearchSongBinding
import me.wcy.music.discover.playlist.detail.bean.NmAlbumData
import me.wcy.music.utils.MusicUtils
import me.wcy.radapter3.RItemBinder
import top.wangchenyan.common.ext.context

/**
 * Created by wangchenyan.top on 2023/9/20.
 */
class SearchAlbumItemBinder(private val listener: OnItemClickListener2<NmAlbumData>) :
    RItemBinder<ItemSearchSongBinding, NmAlbumData>() {
    var keywords = ""

    override fun onBind(viewBinding: ItemSearchSongBinding, item: NmAlbumData, position: Int) {
        viewBinding.root.setOnClickListener {
            listener.onItemClick(item, position)
        }
        viewBinding.ivMore.setOnClickListener {
            listener.onMoreClick(item, position)
        }
        viewBinding.tvTitle.text = MusicUtils.keywordsTint(viewBinding.context, item.name, keywords)
//        viewBinding.tvTag.isVisible = item.recommendReason.isNotEmpty()
//        viewBinding.tvTag.text = item.recommendReason
        viewBinding.tvSubTitle.text = buildString {
            append(item.artist)
        }
        viewBinding.ivMore.isGone = true

    }
}