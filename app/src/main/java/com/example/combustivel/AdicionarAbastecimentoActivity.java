package com.example.combustivel;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdicionarAbastecimentoActivity extends AppCompatActivity {

    private TextInputEditText editKms, editPrecoUnidade, editPrecoTotal;
    private TextInputLayout layoutPrecoUnidade;
    private TextView tvTitulo;
    private Button btnGuardar;

    private AppBaseDados mDb;
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    private int veiculoId;
    private Veiculo veiculoAtual;

    private boolean modoEditar = false;
    private int abastecimentoIdParaEditar = -1;
    private Abastecimento abastecimentoAtual = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_abastecimento);

        mDb = AppBaseDados.getDatabase(getApplicationContext());

        // Ligar variaveis
        editKms = findViewById(R.id.kms);
        editPrecoTotal = findViewById(R.id.preco_total);
        editPrecoUnidade = findViewById(R.id.preco_unidade);
        layoutPrecoUnidade = findViewById(R.id.layout_preco_unidade);
        tvTitulo = findViewById(R.id.tv_titulo);
        btnGuardar = findViewById(R.id.btn_guardar_abastecimento);

        veiculoId = getIntent().getIntExtra("VEICULO_ID", -1);
        abastecimentoIdParaEditar = getIntent().getIntExtra("EXTRA_ABASTECIMENTO_ID", -1);

        if (veiculoId == -1) {
            Toast.makeText(this, "Erro: ID do Veículo não encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Decidir se estamos em modo ADICIONAR ou EDITAR
        if (abastecimentoIdParaEditar != -1) {
            modoEditar = true;
            tvTitulo.setText("Editar Registo");
            btnGuardar.setText("Atualizar Registo");
            carregarDadosDoAbastecimento(abastecimentoIdParaEditar);
        } else {
            modoEditar = false;
            tvTitulo.setText("Novo Abastecimento");
            btnGuardar.setText("Guardar Abastecimento");
            carregarTipoVeiculo();
        }

        btnGuardar.setOnClickListener(v -> guardarAbastecimento());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    // Modo ADICIONAR
    private void carregarTipoVeiculo() {
        databaseExecutor.execute(() -> {
            veiculoAtual = mDb.veiculoDao().getVeiculoById(veiculoId);
            runOnUiThread(() -> {
                if (veiculoAtual == null) { return; }
                definirHints(veiculoAtual.getTipoVeiculo());
            });
        });
    }

    // Modo EDITAR
    private void carregarDadosDoAbastecimento(int id) {
        databaseExecutor.execute(() -> {
            abastecimentoAtual = mDb.abastecimentoDao().getAbastecimentoById(id);
            veiculoAtual = mDb.veiculoDao().getVeiculoById(veiculoId);

            runOnUiThread(() -> {
                if (abastecimentoAtual == null || veiculoAtual == null) {
                    Toast.makeText(this, "Erro ao carregar dados", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                definirHints(veiculoAtual.getTipoVeiculo());

                editKms.setText(String.format(Locale.getDefault(), "%.1f", abastecimentoAtual.kilometros));
                editPrecoTotal.setText(String.format(Locale.getDefault(), "%.2f", abastecimentoAtual.custoTotal));

                double precoPorUnidade = (abastecimentoAtual.litros > 0) ? (abastecimentoAtual.custoTotal / abastecimentoAtual.litros) : 0.0;
                editPrecoUnidade.setText(String.format(Locale.getDefault(), "%.3f", precoPorUnidade));
            });
        });
    }

    private void definirHints(String tipoVeiculo) {
        if (tipoVeiculo.equals("ELETRICO")) {
            layoutPrecoUnidade.setHint("Preço por kWh");
        } else {
            layoutPrecoUnidade.setHint("Preço por Litro");
        }
    }

    private void guardarAbastecimento() {
        String kmTexto = editKms.getText().toString();
        String precoUnidadeTexto = editPrecoUnidade.getText().toString();
        String precoTotalTexto = editPrecoTotal.getText().toString();

        if (kmTexto.isEmpty() || precoUnidadeTexto.isEmpty() || precoTotalTexto.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double kilometros = Double.parseDouble(kmTexto);
            double precoPorUnidade = Double.parseDouble(precoUnidadeTexto);
            double custoTotal = Double.parseDouble(precoTotalTexto);

            if (precoPorUnidade == 0) {
                Toast.makeText(this, "O preço por unidade não pode ser zero", Toast.LENGTH_SHORT).show();
                return;
            }

            double unidades = custoTotal / precoPorUnidade;

            databaseExecutor.execute(() -> {
                if (modoEditar) {
                    abastecimentoAtual.kilometros = kilometros;
                    abastecimentoAtual.litros = unidades;
                    abastecimentoAtual.custoTotal = custoTotal;
                    mDb.abastecimentoDao().update(abastecimentoAtual);

                    runOnUiThread(() -> {
                        Toast.makeText(this, "Registo atualizado!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                } else {
                    long dataAtual = System.currentTimeMillis();
                    Abastecimento novoAbastecimento = new Abastecimento(veiculoId, kilometros, unidades, custoTotal, dataAtual);
                    mDb.abastecimentoDao().insert(novoAbastecimento);

                    runOnUiThread(() -> {
                        Toast.makeText(this, "Registo guardado!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            });

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Valores numéricos inválidos", Toast.LENGTH_SHORT).show();
        }
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