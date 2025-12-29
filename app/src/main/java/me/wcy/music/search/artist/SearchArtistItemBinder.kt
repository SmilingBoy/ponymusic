package me.wcy.music.search.artist

import androidx.core.view.isGone
import me.wcy.music.common.OnItemClickListener2
import me.wcy.music.databinding.ItemSearchSongBinding
import me.wcy.music.discover.artist.bean.NmArtistData
import me.wcy.music.utils.MusicUtils
import me.wcy.radapter3.RItemBinder
import top.wangchenyan.common.ext.context

/**
 * Created by wangchenyan.top on 2023/9/20.
 */
class SearchArtistItemBinder(private val listener: OnItemClickListener2<NmArtistData>) :
    RItemBinder<ItemSearchSongBinding, NmArtistData>() {
    var keywords = ""

    override fun onBind(viewBinding: ItemSearchSongBinding, item: NmArtistData, position: Int) {
        viewBinding.root.setOnClickListener {
            listener.onItemClick(item, position)
        }
        viewBinding.ivMore.setOnClickListener {
            listener.onMoreClick(item, position)
        }
        viewBinding.tvTitle.text = MusicUtils.keywordsTint(viewBinding.context, item.name, keywords)
//        viewBinding.tvTag.isVisible = item.recommendReason.isNotEmpty()
//        viewBinding.tvTag.text = item.recommendReason
        viewBinding.ivMore.isGone = true

        viewBinding.tvSubTitle.text = "${item.songCount}首歌曲, ${item.albumCount}张专辑"

    }
}