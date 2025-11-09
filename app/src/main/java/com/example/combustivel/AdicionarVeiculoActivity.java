package com.example.combustivel;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Ecra que criei para ser responsavel para criar um novo veiculo, funciona tambme como um formulario

public class AdicionarVeiculoActivity extends AppCompatActivity {

    // Referencias para os campos de texto definidos no XML
    private TextInputEditText editNome, editMarca, editModelo;
    // Referencia para o botao de guardar
    private Button btnGuardar;

    // A  instância da base de dados Room (AppBaseDados)
    private AppBaseDados mDb;

    // O ExecutorService é importante porque o android proibe operaçoes na thread principal.
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    // metodo "OnCreate, usado quando é criado o ecra

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Liga este ficheiro Java ao seu ficheiro de layout XML
        setContentView(R.layout.activity_adicionar_veiculo);

        // Inicializa a instância da base de dados
        mDb = AppBaseDados.getDatabase(getApplicationContext());

        // Liga as  variáveis Java aos elementos (Views) definidos no xml atraves dos ids
        editNome = findViewById(R.id.edit_veiculo_nome);
        editMarca = findViewById(R.id.edit_veiculo_marca);
        editModelo = findViewById(R.id.edit_veiculo_modelo);
        btnGuardar = findViewById(R.id.btn_guardar_veiculo);

        // Define o que acontece quando o botão "guardar" é clicado.
        btnGuardar.setOnClickListener(v -> {
            // Quando clicado, chama o  metodo  'guardarVeiculo()'.
            guardarVeiculo();
        });

        //Ativa o botão "Voltar" na barra de topo
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    // metodo que contem a logica para validar os dados e guardar na base de dados

    private void guardarVeiculo() {
        //Le o texto que o utilizador escreveu em cada campo e o trim tira espaços desnecessarios
        String nome = editNome.getText().toString().trim();
        String marca = editMarca.getText().toString().trim();
        String modelo = editModelo.getText().toString().trim();

        // Verificar se os campos estão vazios.
        if (nome.isEmpty()) {
            // Se estiver vazio, mostra uma mensagem de aviso
            Toast.makeText(this, "O nome do veículo é obrigatório", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cria um novo objeto "Veiculo" com os dados validados.
        Veiculo novoVeiculo = new Veiculo(nome, marca, modelo);

        // Executa a operação da base de dados na thread separada.
        databaseExecutor.execute(() -> {
            // Este código corre em "background".

            // Usa o DAO para executar o comando @Insert e para guardar o novo veiculo na tabela
            mDb.veiculoDao().insert(novoVeiculo);

            // Depois de guardar, vai voltar à 'thread' principal (UI thread)
            runOnUiThread(() -> {
                Toast.makeText(this, "Veículo guardado!", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    // Metodo que usamos para programar a ação da seta 'Voltar' na barra de topo

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