package com.example.combustivel;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// a classe (Room) é a classe principal da Base de dados, ela vai definir quais é que sao as tabelas
// vai definir qual é a versao da base de dados, vai fornecer acesso ao (DAO)
//e vai implenetar o padrao "Singleton" de forma a garantir que só hã uma instancia da base de dados

@Database(entities = {Abastecimento.class, Veiculo.class}, version = 2)
public abstract class AppBaseDados extends RoomDatabase {

//Vai ligar aos (DAOS)
    // Métodos  que o Room vai implementar automaticamente
    public abstract AbastecimentoDao abastecimentoDao();
    public abstract VeiculoDao veiculoDao();


    // Implementação do Padrão Singleton

    private static volatile AppBaseDados INSTANCE;

    // Metodo estatico que vai devolver a instancia Singleton da base dados que tambem é o metodo que
    // as classes vao chamar para aceder a base de dados

    static AppBaseDados getDatabase(final Context context) {

        //Verificação
        // Se a instância já existe, devolve-a
        if (INSTANCE == null) {

            //Verificação
            // Se a instância é nula, entramos num bloco que é o synchornized
            // Se duas threads tentam criar a BD ao mesmo tempo a primeira vai trancar para a segunda esperar
            synchronized (AppBaseDados.class) {

                //Verificação
                // A thread que ficou à espera, quando entrar vai verificar se a instancia ainda é nula
                if (INSTANCE == null) {

                    // Se a instância é mesmo nula, criamos a base de dados.
                    INSTANCE = Room.databaseBuilder(
                                    // Usamos o 'applicationContext' para evitar fugas de memoria
                                    context.getApplicationContext(),
                                    AppBaseDados.class, // A classe da BD
                                    "combustivel_database" // O nome do ficheiro físico no telemóvel
                            )
                            // Regra de Migração
                            // Isto diz ao Room: "Se a versao  mudar, apaga a base de dados antiga e cria uma nova."
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }

        // Devolve a instância
        return INSTANCE;
    }
}