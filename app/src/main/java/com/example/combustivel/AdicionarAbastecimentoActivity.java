package com.example.combustivel;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import java.util.concurrent.ExecutorService; // Import para a thread da Base de Dados
import java.util.concurrent.Executors; // Import para a thread da Base de Dados

// Ecra que criei para adicionar um novo abastecimento, praticamente é um formulario, guarda o abastecimento na base de dados room neste caso

public class AdicionarAbastecimentoActivity extends AppCompatActivity {

    // Variaveis da Interface
    // Campos de texto onde o utilizador insere os dados
    private TextInputEditText editKms, editPrecoLitro, editPrecoTotal;

    // O botao para guardar
    private Button btnGuardar;

    //Variaveis da Base de Dados
    // A instancia da nossa base de dados (Room)
    private AppBaseDados mDb;

    // O 'ExecutorService' e o que nos permite correr a base de dados
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    // Variavel para guardar o ID do veiculo a que este abastecimento pertence
    private int veiculoId;

    // metodo "OnCreate chamado quando o ecra é ligado e é assim que ligamos o layout xml e configurar os cliques

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ligar este ficheiro Java ao seu layout XML
        setContentView(R.layout.activity_adicionar_abastecimento);

        // Iniciar a instancia da base de dados
        mDb = AppBaseDados.getDatabase(getApplicationContext());

        // Ligar as variaveis Java aos IDs dos elementos no XML
        editKms = findViewById(R.id.kms);
        editPrecoLitro = findViewById(R.id.preco_litro);
        editPrecoTotal = findViewById(R.id.preco_total);
        btnGuardar = findViewById(R.id.btn_guardar_abastecimento);

        // Vai buscar o ID do veiculo que o DetalhesVeiculoActivity enviou, o "get intent" e a "mala" tranporta dados entre ecras, e se nao achar nada devolve -1
        veiculoId = getIntent().getIntExtra("VEICULO_ID", -1);

        //Se o ID nao foi encontrado, mostra um erro e fecha
        if (veiculoId == -1) {
            // Mostrar um erro e fechar este ecra para evitar um "crash"
            Toast.makeText(this, "Erro: ID do Veículo não encontrado", Toast.LENGTH_SHORT).show();
            finish(); // Fecha o ecra
            return; // Para a execucao do onCreate
        }

        // 6. Configurar o "ouvinte" de clique do botao guardar
        btnGuardar.setOnClickListener(v -> {
            // Quando o botao for clicado, chama o  metodo "guardarAbastecimento"
            guardarAbastecimento();
        });

        // 7. Ativar a seta "Voltar" na barra de topo
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    // Metodo que contem toda a logica para guardar o abastecimento

    private void guardarAbastecimento() {

        // Le o texto que o utilizador escreveu nos campos
        String kmTexto = editKms.getText().toString();
        String precoLitroTexto = editPrecoLitro.getText().toString();
        String precoTotalTexto = editPrecoTotal.getText().toString();

        //Verifica se algum campo esta vazio
        if (kmTexto.isEmpty() || precoLitroTexto.isEmpty() || precoTotalTexto.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return; // Para a execucao aqui
        }

        // Usar um try porque a conversao de String para Double pode falhar
        try {
            // Converter os textos para numeros (double)
            double kilometros = Double.parseDouble(kmTexto);
            double precoporlitro = Double.parseDouble(precoLitroTexto);
            double custoTotal = Double.parseDouble(precoTotalTexto);
            long dataAtual = System.currentTimeMillis(); // Guarda a data/hora atual

            // Valida para evitar uma divisao por zero
            if (precoporlitro == 0) {
                Toast.makeText(this, "O preço por litro não pode ser zero", Toast.LENGTH_SHORT).show();
                return;
            }

            // Calcula os litros com base nos dados do utilizador
            double litros = custoTotal / precoporlitro;

            //Cria um novo objeto com todos os dados
            Abastecimento novoAbastecimento = new Abastecimento(veiculoId, kilometros, litros, custoTotal, dataAtual);

            //Guarda na Base de Dados usando a thread
            databaseExecutor.execute(() -> {
                //DAO para inserir o novo registo na tabela

                mDb.abastecimentoDao().insert(novoAbastecimento);

                // Depois de guardar, volta para a thread principal para mostrar o toast e fechar o ecra
                runOnUiThread(() -> {
                    Toast.makeText(this, "Abastecimento guardado!", Toast.LENGTH_SHORT).show();
                    finish(); // Fecha este ecra
                });
            });

        } catch (NumberFormatException e) {
            // Se a conversao falhar, mostra um erro
            Toast.makeText(this, "Valores numéricos inválidos", Toast.LENGTH_SHORT).show();
        }
    }

    // Metodo que e chamado quando o utilziador clica na seta voltar na barra

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Verifica se o item clicado é a seta de voltar
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}