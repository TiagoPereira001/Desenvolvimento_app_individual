package com.example.combustivel;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

@Entity(tableName = "tabela_abastecimentos",
        foreignKeys = @ForeignKey(entity = Veiculo.class,
                parentColumns = "id",
                childColumns = "veiculo_id",
                onDelete = ForeignKey.CASCADE))
public class Abastecimento {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "veiculo_id", index = true)
    public int veiculoId;

    public double kilometros;
    public double litros;
    public double custoTotal;
    public long data;

    // Este e o construtor que o Room vai usar
    public Abastecimento() {}

    // Este e o construtor que eu uso, e diz ao Room para ignorar o delete
    @Ignore
    public Abastecimento(int veiculoId, double kilometros, double litros, double custoTotal, long data) {
        this.veiculoId = veiculoId;
        this.kilometros = kilometros;
        this.litros = litros;
        this.custoTotal = custoTotal;
        this.data = data;
    }
}