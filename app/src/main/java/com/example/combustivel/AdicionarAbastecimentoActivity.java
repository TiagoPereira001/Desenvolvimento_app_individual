package com.example.combustivel;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdicionarAbastecimentoActivity extends AppCompatActivity {

    // --- MUDANÇA AQUI ---
    // A variavel 'editLitros' desaparece
    private TextInputEditText editKms, editPrecoLitro, editPrecoTotal;
    // --- FIM DA MUDANÇA ---

    private Button btnGuardar;
    private AppBaseDados mDb;
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();
    private int veiculoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_abastecimento);

        mDb = AppBaseDados.getDatabase(getApplicationContext());

        // --- MUDANÇA AQUI ---
        // Ligar as variaveis aos IDs corretos do XML
        editKms = findViewById(R.id.kms);
        editPrecoLitro = findViewById(R.id.preco_litro);
        editPrecoTotal = findViewById(R.id.preco_total); // O antigo 'editLitros'
        // A linha "editLitros = findViewById(R.id.ltrs);" desaparece
        // --- FIM DA MUDANÇA ---

        btnGuardar = findViewById(R.id.btn_guardar_abastecimento);

        veiculoId = getIntent().getIntExtra("VEICULO_ID", -1);
        if (veiculoId == -1) {
            Toast.makeText(this, "Erro: ID do Veículo não encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnGuardar.setOnClickListener(v -> {
            guardarAbastecimento();
        });

        // Codigo para mostrar a seta "Voltar"
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void guardarAbastecimento() {
        // --- MUDANÇA AQUI ---
        // Ler os textos dos campos corretos
        String kmTexto = editKms.getText().toString();
        String precoLitroTexto = editPrecoLitro.getText().toString();
        String precoTotalTexto = editPrecoTotal.getText().toString();

        // Validacao
        if (kmTexto.isEmpty() || precoLitroTexto.isEmpty() || precoTotalTexto.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }
        // --- FIM DA MUDANÇA ---

        try {
            // Converter para numeros
            double kilometros = Double.parseDouble(kmTexto);
            double precoporlitro = Double.parseDouble(precoLitroTexto);
            double custoTotal = Double.parseDouble(precoTotalTexto);
            long dataAtual = System.currentTimeMillis();

            // --- MUDANCA DE LOGICA (O CALCULO) ---

            // Validar para nao dividir por zero
            if (precoporlitro == 0) {
                Toast.makeText(this, "O preço por litro não pode ser zero", Toast.LENGTH_SHORT).show();
                return;
            }

            // Calcular os litros
            double litros = custoTotal / precoporlitro;
            // --- FIM DA MUDANCA ---

            // Criar o objeto Abastecimento com os dados corretos
            Abastecimento novoAbastecimento = new Abastecimento(veiculoId, kilometros, litros, custoTotal, dataAtual);

            // Guardar na BD (noutra thread)
            databaseExecutor.execute(() -> {
                mDb.abastecimentoDao().insert(novoAbastecimento);

                // Voltar a thread principal para fechar o ecra
                runOnUiThread(() -> {
                    Toast.makeText(this, "Abastecimento guardado!", Toast.LENGTH_SHORT).show();
                    finish(); // Fecha e volta ao ecra de Detalhes
                });
            });

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Valores numéricos inválidos", Toast.LENGTH_SHORT).show();
        }
    }

    // Codigo para fazer a seta "Voltar" funcionar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}