package me.wcy.music.discover.playlist.detail.item

import me.wcy.music.common.OnItemClickListener2
import me.wcy.music.common.bean.SongData
import me.wcy.music.databinding.ItemPlaylistSongBinding
import me.wcy.music.discover.playlist.detail.bean.NmSongData
import me.wcy.music.utils.getSimpleArtist
import me.wcy.radapter3.RItemBinder

/**
 * Created by wangchenyan.top on 2023/9/22.
 */
class PlaylistSongItemBinder(private val listener: OnItemClickListener2<NmSongData>) :
    RItemBinder<ItemPlaylistSongBinding, NmSongData>() {

    override fun onBind(viewBinding: ItemPlaylistSongBinding, item: NmSongData, position: Int) {
        viewBinding.root.setOnClickListener {
            listener.onItemClick(item, position)
        }
        viewBinding.ivMore.setOnClickListener {
            listener.onMoreClick(item, position)
        }
        viewBinding.tvIndex.text = (position + 1).toString()
        viewBinding.tvTitle.text = item.title
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