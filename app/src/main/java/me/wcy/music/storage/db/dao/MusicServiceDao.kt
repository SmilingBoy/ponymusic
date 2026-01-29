package me.wcy.music.storage.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import me.wcy.music.storage.db.entity.MusicServiceEntity

@Dao
interface MusicServiceDao {
    @Insert
    suspend fun insert(service: MusicServiceEntity)

    @Delete
    suspend fun delete(service: MusicServiceEntity)

    @Query("SELECT * FROM music_server")
    suspend fun getAll(): List<MusicServiceEntity>

    @Query("SELECT * FROM music_server WHERE is_default = 1")
    suspend fun getDefault(): MusicServiceEntity?

    @Query("UPDATE music_server SET is_default = 0")
    suspend fun resetDefault()

    @Query("UPDATE music_server SET is_default = 1 WHERE id = :id")
    suspend fun setDefault(id: Long)
}
