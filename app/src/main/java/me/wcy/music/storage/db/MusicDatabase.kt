package me.wcy.music.storage.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import me.wcy.music.storage.db.dao.MusicServiceDao
import me.wcy.music.storage.db.dao.PlaylistDao
import me.wcy.music.storage.db.entity.MusicServiceEntity
import me.wcy.music.storage.db.entity.SongEntity

/**
 * Created by wangchenyan.top on 2023/8/29.
 */
@Database(
    entities = [
        SongEntity::class,
        MusicServiceEntity::class,
    ],
    version = 3,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3)
    ]
)
abstract class MusicDatabase : RoomDatabase() {

    abstract fun playlistDao(): PlaylistDao
    abstract fun musicServiceDao(): MusicServiceDao
}