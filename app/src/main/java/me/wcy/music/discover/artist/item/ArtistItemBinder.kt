package me.wcy.music.discover.artist.item

import androidx.core.view.isVisible
import com.blankj.utilcode.util.SizeUtils
import me.wcy.music.databinding.ItemArtistBinding
import me.wcy.music.discover.artist.bean.NmArtistData
import me.wcy.music.utils.ImageUtils.loadCover
import me.wcy.music.utils.NavidromeUtil
import me.wcy.radapter3.RItemBinder

/**
 * 歌手列表项Binder
 */
class ArtistItemBinder(private val listener: OnItemClickListener) :
    RItemBinder<ItemArtistBinding, NmArtistData>() {

    interface OnItemClickListener {
        fun onItemClick(item: NmArtistData)
        fun onMoreClick(item: NmArtistData)
    }

    override fun onBind(viewBinding: ItemArtistBinding, item: NmArtistData, position: Int) {
        viewBinding.root.setOnClickListener {
            listener.onItemClick(item)
        }

        // 加载歌手封面
        viewBinding.ivCover.loadCover(NavidromeUtil.getCover(item.id), SizeUtils.dp2px(4f))
        viewBinding.tvName.text = item.name
        viewBinding.tvCount.text = "${item.songCount}首歌曲, ${item.albumCount}张专辑"
        viewBinding.ivMore.setOnClickListener {
            listener.onMoreClick(item)
        }
    }
}