package com.example.combustivel;

import android.os.Bundle;
import android.view.MenuItem; // IMPORTAR PARA O BOTAO VOLTAR
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdicionarVeiculoActivity extends AppCompatActivity {

    private TextInputEditText editNome, editMarca, editModelo;
    private Button btnGuardar;
    private AppBaseDados mDb;
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_veiculo);

        mDb = AppBaseDados.getDatabase(getApplicationContext());

        editNome = findViewById(R.id.edit_veiculo_nome);
        editMarca = findViewById(R.id.edit_veiculo_marca);
        editModelo = findViewById(R.id.edit_veiculo_modelo);
        btnGuardar = findViewById(R.id.btn_guardar_veiculo);

        btnGuardar.setOnClickListener(v -> {
            guardarVeiculo();
        });

        // CODIGO PARA MOSTRAR A SETA "VOLTAR" NA BARRA
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void guardarVeiculo() {
        String nome = editNome.getText().toString().trim();
        String marca = editMarca.getText().toString().trim();
        String modelo = editModelo.getText().toString().trim();

        if (nome.isEmpty()) {
            Toast.makeText(this, "O nome do veículo é obrigatório", Toast.LENGTH_SHORT).show();
            return;
        }

        Veiculo novoVeiculo = new Veiculo(nome, marca, modelo);

        databaseExecutor.execute(() -> {
            mDb.veiculoDao().insert(novoVeiculo);
            runOnUiThread(() -> {
                Toast.makeText(this, "Veículo guardado!", Toast.LENGTH_SHORT).show();
                finish(); // Fecha o ecra
            });
        });
    }

    // CODIGO PARA FAZER A SETA "VOLTAR" FUNCIONAR
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Fecha este ecra e volta ao "pai" (MainActivity)
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}