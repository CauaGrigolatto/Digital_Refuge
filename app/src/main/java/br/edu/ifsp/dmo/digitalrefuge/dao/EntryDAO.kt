package br.edu.ifsp.dmo.digitalrefuge.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.edu.ifsp.dmo.digitalrefuge.model.Entry

@Dao
interface EntryDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: Entry)

    @Update
    suspend fun update(entry: Entry)

    @Delete
    suspend fun delete(entry: Entry)

    @Query("SELECT * FROM entry WHERE id = :id")
    suspend fun findById(id: Long): Entry?

    @Query("SELECT * FROM entry ORDER BY date DESC")
    suspend fun getAll(): List<Entry>
}
