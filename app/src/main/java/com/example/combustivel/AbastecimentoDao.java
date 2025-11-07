package com.example.combustivel;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;
// o ficheiro abastecimentoDao e o veiculoDao sao documentos importantes para a base de dados da aplicação
@Dao
public interface AbastecimentoDao {

    @Insert
    void insert(Abastecimento abastecimento);

    @Query("SELECT * FROM tabela_abastecimentos WHERE veiculo_id = :idDoVeiculo ORDER BY data DESC")
    List<Abastecimento> getAbastecimentosDoVeiculo(int idDoVeiculo);
}