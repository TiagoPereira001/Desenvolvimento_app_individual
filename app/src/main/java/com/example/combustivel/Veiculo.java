package com.example.combustivel;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore; // <-- 1. IMPORTAR ISTO

@Entity(tableName = "tabela_veiculos")
public class Veiculo {

    @PrimaryKey(autoGenerate = true)
    public int id;
    public String nome;
    public String marca;
    public String modelo;

    // Este e o construtor que o Room vai usar
    public Veiculo() {}

    // 2. ADICIONAR @IGNORE AQUI
    // Este e o construtor que NOS usamos
    @Ignore
    public Veiculo(String nome, String marca, String modelo) {
        this.nome = nome;
        this.marca = marca;
        this.modelo = modelo;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
}