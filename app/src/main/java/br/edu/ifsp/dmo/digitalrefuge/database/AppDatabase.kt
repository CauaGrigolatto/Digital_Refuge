package br.edu.ifsp.dmo.digitalrefuge.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.edu.ifsp.dmo.digitalrefuge.dao.EntryDAO
import br.edu.ifsp.dmo.digitalrefuge.model.Entry

@Database(entities = [Entry::class], version = 2) //versão mudada para suportar a alteração do BD
abstract class AppDatabase : RoomDatabase() {
    abstract fun entryDao(): EntryDAO

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "refugio.db"
                )
                .fallbackToDestructiveMigration()
                .build().also { instance = it }
            }
    }
}
