package com.example.combustivel;

import androidx.room.Dao;
import androidx.room.Delete; // <-- IMPORTAR ISTO
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface VeiculoDao {

    @Insert
    void insert(Veiculo veiculo);

    // --- ADICIONAR ESTE MÉTODO ---
    // O @Delete e magico. Passamos-lhe um objeto Veiculo
    // e o Room trata de o encontrar na BD e apaga-lo.
    @Delete
    void delete(Veiculo veiculo);
    // --- FIM DA ADIÇÃO ---

    @Query("SELECT * FROM tabela_veiculos ORDER BY nome ASC")
    List<Veiculo> getAllVeiculos();

    @Query("SELECT * FROM tabela_veiculos WHERE id = :id LIMIT 1")
    Veiculo getVeiculoById(int id);
}