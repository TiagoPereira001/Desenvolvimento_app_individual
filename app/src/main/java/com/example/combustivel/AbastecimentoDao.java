package com.example.combustivel;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface AbastecimentoDao {
    @Insert
    void insert(Abastecimento abastecimento);
    @Update
    void update(Abastecimento abastecimento);
    @Delete
    void delete(Abastecimento abastecimento);
    @Query("SELECT * FROM tabela_abastecimentos WHERE id = :abastecimentoId LIMIT 1")
    Abastecimento getAbastecimentoById(int abastecimentoId);
    @Query("SELECT * FROM tabela_abastecimentos WHERE veiculo_id = :idDoVeiculo ORDER BY data DESC")
    List<Abastecimento> getAbastecimentosDoVeiculo(int idDoVeiculo);
}