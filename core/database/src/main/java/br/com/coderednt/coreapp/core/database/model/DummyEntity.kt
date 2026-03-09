package br.com.coderednt.coreapp.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dummy_table")
data class DummyEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "dummy"
)
