package com.example.combustivel;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Abastecimento.class, Veiculo.class}, version = 2)
public abstract class AppBaseDados extends RoomDatabase {

    public abstract AbastecimentoDao abastecimentoDao();
    public abstract VeiculoDao veiculoDao();

    private static volatile AppBaseDados INSTANCE;

    static AppBaseDados getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppBaseDados.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppBaseDados.class, "combustivel_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}