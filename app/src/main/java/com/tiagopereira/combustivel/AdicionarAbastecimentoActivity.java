package com.tiagopereira.combustivel;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
    // Adicionar referencias aos layouts para mostrar erros
    private TextInputLayout layoutKms, layoutPrecoUnidade, layoutPrecoTotal;

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

        // Ligar os layouts (Tens de garantir que os IDs no XML correspondem, ou adicionar IDs aos TextInputLayouts se nao tiverem)
        // Nota: No teu XML original, os IDs layout_kms, layout_preco_unidade, etc já existem. Perfeito.
        layoutKms = findViewById(R.id.layout_kms);
        layoutPrecoUnidade = findViewById(R.id.layout_preco_unidade);
        layoutPrecoTotal = findViewById(R.id.layout_preco_total);

        tvTitulo = findViewById(R.id.tv_titulo);
        btnGuardar = findViewById(R.id.btn_guardar_abastecimento);

        // Limpar erros quando o utilizador começa a escrever
        setupTextWatchers();

        veiculoId = getIntent().getIntExtra("VEICULO_ID", -1);
        abastecimentoIdParaEditar = getIntent().getIntExtra("EXTRA_ABASTECIMENTO_ID", -1);

        if (veiculoId == -1) {
            Toast.makeText(this, "Erro: ID do Veículo não encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (abastecimentoIdParaEditar != -1) {
            modoEditar = true;
            tvTitulo.setText(getString(R.string .editar_registo)); // Usa strings.xml!
            btnGuardar.setText(getString(R.string.atualizar_registo));
            carregarDadosDoAbastecimento(abastecimentoIdParaEditar);
        } else {
            modoEditar = false;
            tvTitulo.setText(getString(R.string.novo_abastecimento));
            btnGuardar.setText(getString(R.string.guardar_abastecimento));
            carregarTipoVeiculo();
        }

        btnGuardar.setOnClickListener(v -> guardarAbastecimento());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupTextWatchers() {
        TextWatcher clearErrorWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                layoutKms.setError(null);
                layoutPrecoUnidade.setError(null);
                layoutPrecoTotal.setError(null);
            }
        };

        editKms.addTextChangedListener(clearErrorWatcher);
        editPrecoUnidade.addTextChangedListener(clearErrorWatcher);
        editPrecoTotal.addTextChangedListener(clearErrorWatcher);
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

                editKms.setText(String.format(Locale.US, "%.1f", abastecimentoAtual.kilometros)); // Locale.US para garantir ponto em vez de virgula no codigo
                editPrecoTotal.setText(String.format(Locale.US, "%.2f", abastecimentoAtual.custoTotal));

                double precoPorUnidade = (abastecimentoAtual.litros > 0) ? (abastecimentoAtual.custoTotal / abastecimentoAtual.litros) : 0.0;
                editPrecoUnidade.setText(String.format(Locale.US, "%.3f", precoPorUnidade));
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

        boolean temErro = false;

        if (kmTexto.isEmpty()) {
            layoutKms.setError("Insira os quilómetros");
            temErro = true;
        }

        if (precoUnidadeTexto.isEmpty()) {
            layoutPrecoUnidade.setError("Insira o preço unitário");
            temErro = true;
        }

        if (precoTotalTexto.isEmpty()) {
            layoutPrecoTotal.setError("Insira o total pago");
            temErro = true;
        }

        if (temErro) return;

        try {
            double kilometros = Double.parseDouble(kmTexto.replace(",", "."));
            double precoPorUnidade = Double.parseDouble(precoUnidadeTexto.replace(",", "."));
            double custoTotal = Double.parseDouble(precoTotalTexto.replace(",", "."));

            if (precoPorUnidade <= 0) {
                layoutPrecoUnidade.setError("O preço deve ser maior que 0");
                return;
            }

            if (kilometros < 0) {
                layoutKms.setError("Kms não podem ser negativos");
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
            Toast.makeText(this, "Valores inválidos. Use ponto (.) ou vírgula (,)", Toast.LENGTH_SHORT).show();
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