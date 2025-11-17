package com.example.combustivel;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "tabela_veiculos")
public class Veiculo {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String nome;
    public String marca;
    public String modelo;
    public String tipoVeiculo;
    public double capacidadeBateria;

    public Veiculo() {}

    @Ignore
    public Veiculo(String nome, String marca, String modelo, String tipoVeiculo, double capacidadeBateria) {
        this.nome = nome;
        this.marca = marca;
        this.modelo = modelo;
        this.tipoVeiculo = tipoVeiculo;
        this.capacidadeBateria = capacidadeBateria;
    }
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public String getTipoVeiculo() { return tipoVeiculo; }
    public double getCapacidadeBateria() { return capacidadeBateria; }
}