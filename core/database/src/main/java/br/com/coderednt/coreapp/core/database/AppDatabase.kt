package br.com.coderednt.coreapp.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.coderednt.coreapp.core.database.model.DummyEntity

/**
 * Banco de dados principal do SDK utilizando Room.
 * 
 * Esta classe centraliza o acesso a todas as entidades persistentes do ecossistema.
 * O esquema é exportado para permitir migrações seguras e testes de banco de dados.
 */
@Database(
    entities = [DummyEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    /**
     * Os DAOs devem ser adicionados aqui conforme as novas funcionalidades 
     * de persistência forem implementadas.
     */
}
