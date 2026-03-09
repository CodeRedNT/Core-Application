package br.com.coderednt.coreapp.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.coderednt.coreapp.core.database.model.DummyEntity

@Database(
    entities = [DummyEntity::class], // Adicionado DummyEntity para satisfazer o KSP
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    // DAOs serão adicionados conforme a necessidade das features
}
