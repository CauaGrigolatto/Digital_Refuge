package br.edu.ifsp.dmo.digitalrefuge.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entry")
data class Entry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val text: String,
    val photoUri: String?
)