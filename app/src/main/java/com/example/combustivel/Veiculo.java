package com.example.combustivel;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

// ecra que a base de dados room usa para guardar os dados dos veiculos


@Entity(tableName = "tabela_veiculos")
public class Veiculo {

    // o ID é unico para cada veiculo

    @PrimaryKey(autoGenerate = true)
    public int id;

    // O Room reconhece automaticamente estes campos públicos como colunas na tabela
    public String nome;
    public String marca;
    public String modelo;


    //Construtores

  // o room precisa de um construtor vazio para funcionar
    public Veiculo() {}

    // @ignore dz ao room para ignorar os construtores para ele nao ficar confuso e nao saber qual usar
    @Ignore
    public Veiculo(String nome, String marca, String modelo) {
        this.nome = nome;
        this.marca = marca;
        this.modelo = modelo;
    }

    // Getters

    public int getId() { return id; }

    public String getNome() { return nome; }

    public String getMarca() { return marca; }

    public String getModelo() { return modelo; }
}