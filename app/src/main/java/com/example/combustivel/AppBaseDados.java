package com.example.combustivel;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Abastecimento.class, Veiculo.class}, version = 4, exportSchema = true)
public abstract class AppBaseDados extends RoomDatabase {

    public abstract AbastecimentoDao abastecimentoDao();
    public abstract VeiculoDao veiculoDao();

    private static volatile AppBaseDados INSTANCE;

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE tabela_veiculos ADD COLUMN tipoVeiculo TEXT");
            database.execSQL("UPDATE tabela_veiculos SET tipoVeiculo = 'COMBUSTAO' WHERE tipoVeiculo IS NULL");
        }
    };

    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE tabela_veiculos "
                    + " ADD COLUMN capacidadeBateria REAL NOT NULL DEFAULT 0.0");
        }
    };

    static AppBaseDados getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppBaseDados.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppBaseDados.class, "combustivel_database")
                            .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}