package me.wcy.music.discover.album.item

import androidx.core.view.isVisible
import com.blankj.utilcode.util.SizeUtils
import me.wcy.music.databinding.ItemAlbumBinding
import me.wcy.music.discover.playlist.detail.bean.NmAlbumData
import me.wcy.music.mine.bean.NmPlaylistBean
import me.wcy.music.utils.ImageUtils.loadCover
import me.wcy.music.utils.NavidromeUtil
import me.wcy.radapter3.RItemBinder

/**
 * 专辑列表项Binder
 */
class AlbumItemBinder(private val listener: OnItemClickListener) :
    RItemBinder<ItemAlbumBinding, NmAlbumData>() {

    interface OnItemClickListener {
        fun onItemClick(item: NmAlbumData)
        fun onMoreClick(item: NmAlbumData)
    }

    override fun onBind(viewBinding: ItemAlbumBinding, item: NmAlbumData, position: Int) {
        viewBinding.root.setOnClickListener {
            listener.onItemClick(item)
        }


        viewBinding.root.setOnClickListener {
            listener.onItemClick(item)
        }
        viewBinding.ivCover.loadCover(NavidromeUtil.getCover(item.id), SizeUtils.dp2px(4f))

//        viewBinding.ivCover.loadCover(item.getSmallCover(), SizeUtils.dp2px(4f))
        viewBinding.tvName.text = item.name
        viewBinding.tvCount.text = "${item.songCount}首, by ${item.albumArtist}"
        viewBinding.ivMore.setOnClickListener {
            listener.onMoreClick(item)
        }

    }
}