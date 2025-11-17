package com.example.combustivel;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdicionarVeiculoActivity extends AppCompatActivity {

    private TextInputEditText editNome, editMarca, editModelo, editCapacidadeBateria;
    private Button btnGuardar;
    private AppBaseDados mDb;
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    private RadioGroup rgTipoVeiculo;
    private TextInputLayout layoutCapacidadeBateria;
    private RadioButton rbEletrico, rbCombustao;
    private String appMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_veiculo);

        mDb = AppBaseDados.getDatabase(getApplicationContext());
        SharedPreferences prefs = getSharedPreferences(ModoActivity.PREFS_NAME, MODE_PRIVATE);
        appMode = prefs.getString(ModoActivity.KEY_APP_MODE, "COMBUSTAO");

        // Ligar componentes
        editNome = findViewById(R.id.edit_veiculo_nome);
        editMarca = findViewById(R.id.edit_veiculo_marca);
        editModelo = findViewById(R.id.edit_veiculo_modelo);
        btnGuardar = findViewById(R.id.btn_guardar_veiculo);
        rgTipoVeiculo = findViewById(R.id.rg_tipo_veiculo);
        layoutCapacidadeBateria = findViewById(R.id.layout_capacidade_bateria);
        editCapacidadeBateria = findViewById(R.id.edit_capacidade_bateria);
        rbEletrico = findViewById(R.id.rb_eletrico);
        rbCombustao = findViewById(R.id.rb_combustao);

        // Logica de visibilidade
        if (appMode.equals("AMBOS")) {
            rgTipoVeiculo.setVisibility(View.VISIBLE);
            setupRadioListeners();
            if (rbEletrico.isChecked()) {
                layoutCapacidadeBateria.setVisibility(View.VISIBLE);
            }
        } else if (appMode.equals("ELETRICO")) {
            rgTipoVeiculo.setVisibility(View.GONE);
            layoutCapacidadeBateria.setVisibility(View.VISIBLE);
        } else {
            rgTipoVeiculo.setVisibility(View.GONE);
            layoutCapacidadeBateria.setVisibility(View.GONE);
        }

        btnGuardar.setOnClickListener(v -> guardarVeiculo());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupRadioListeners() {
        rgTipoVeiculo.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_eletrico) {
                layoutCapacidadeBateria.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.rb_combustao) {
                layoutCapacidadeBateria.setVisibility(View.GONE);
            }
        });
    }

    private void guardarVeiculo() {
        String nome = editNome.getText().toString().trim();
        String marca = editMarca.getText().toString().trim();
        String modelo = editModelo.getText().toString().trim();

        if (nome.isEmpty()) {
            Toast.makeText(this, "O nome do veículo é obrigatório", Toast.LENGTH_SHORT).show();
            return;
        }

        String tipoVeiculo;
        double capacidadeBateria = 0.0;

        if (appMode.equals("AMBOS")) {
            int selectedId = rgTipoVeiculo.getCheckedRadioButtonId();
            tipoVeiculo = (selectedId == R.id.rb_eletrico) ? "ELETRICO" : "COMBUSTAO";
        } else {
            tipoVeiculo = appMode;
        }

        if (tipoVeiculo.equals("ELETRICO")) {
            String capacidadeTexto = editCapacidadeBateria.getText().toString();
            if (capacidadeTexto.isEmpty()) {
                Toast.makeText(this, "Por favor, insira a capacidade da bateria", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                capacidadeBateria = Double.parseDouble(capacidadeTexto);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Capacidade da bateria inválida", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Veiculo novoVeiculo = new Veiculo(nome, marca, modelo, tipoVeiculo, capacidadeBateria);

        databaseExecutor.execute(() -> {
            mDb.veiculoDao().insert(novoVeiculo);
            runOnUiThread(() -> {
                Toast.makeText(this, "Veículo guardado!", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}