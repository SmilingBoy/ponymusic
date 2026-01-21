package me.wcy.music.v2.song

import android.content.Context
import android.view.ViewGroup
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import me.wcy.music.R
import me.wcy.music.discover.playlist.detail.bean.NmSongData

class SongAdapter : BaseQuickAdapter<NmSongData, QuickViewHolder>() {
    override fun onBindViewHolder(
        holder: QuickViewHolder,
        position: Int,
        item: NmSongData?
    ) {

    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        return QuickViewHolder(R.layout.item_song, parent)
    }
}