package com.example.combustivel;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface VeiculoDao {
    @Insert
    void insert(Veiculo veiculo);
    @Delete
    void delete(Veiculo veiculo);
    @Query("SELECT * FROM tabela_veiculos ORDER BY nome ASC")
    List<Veiculo> getAllVeiculos();
    @Query("SELECT * FROM tabela_veiculos WHERE id = :id LIMIT 1")
    Veiculo getVeiculoById(int id);
    @Query("SELECT * FROM tabela_veiculos WHERE tipoVeiculo = :tipo ORDER BY nome ASC")
    List<Veiculo> getVeiculosDeTipo(String tipo);
}