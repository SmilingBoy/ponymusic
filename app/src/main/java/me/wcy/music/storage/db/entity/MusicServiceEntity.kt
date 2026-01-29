package me.wcy.music.storage.db.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
@Entity("music_server")
data class MusicServiceEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Long = 0,
    @ColumnInfo("name")
    val name: String,
    @ColumnInfo("wan_url")
    val wanUrl: String,
    @ColumnInfo("lan_url")
    val lanUrl: String,
    @ColumnInfo("username")
    val username: String,
    @ColumnInfo("password")
    val password: String,
    @ColumnInfo("is_default")
    val isDefault: Boolean
) :  Parcelable