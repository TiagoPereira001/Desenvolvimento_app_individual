package com.example.combustivel;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

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
    public double litros; // Usado para Litros OU kWh
    public double custoTotal;
    public long data;

    public Abastecimento() {}

    @Ignore
    public Abastecimento(int veiculoId, double kilometros, double litros, double custoTotal, long data) {
        this.veiculoId = veiculoId;
        this.kilometros = kilometros;
        this.litros = litros;
        this.custoTotal = custoTotal;
        this.data = data;
    }
}