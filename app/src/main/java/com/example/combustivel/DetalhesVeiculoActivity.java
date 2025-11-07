package com.example.combustivel;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button; // <-- IMPORTAR BOTAO
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog; // <-- IMPORTAR CAIXA DE DIALOGO
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetalhesVeiculoActivity extends AppCompatActivity {

    // UI
    private TextView tvTitulo, tvKms, tvLitros, tvPreco, tvMedia;
    private RecyclerView rvAbastecimentos;
    private FloatingActionButton fabAddAbastecimento;
    private Button btnApagarVeiculo; // <-- 1. DECLARAR O BOTAO

    // BD
    private AppBaseDados mDb;
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    // Lista e Adaptador
    private AdaptadorAbastecimento adapter;
    private List<Abastecimento> listaDeAbastecimentos = new ArrayList<>();

    // O ID e o OBJETO do veiculo que estamos a ver
    private int veiculoId;
    private Veiculo veiculoAtual; // <-- 2. GUARDAR O VEICULO ATUAL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_veiculo);

        mDb = AppBaseDados.getDatabase(getApplicationContext());

        // Ligar variaveis aos IDs do XML
        tvTitulo = findViewById(R.id.tv_detalhe_titulo);
        tvKms = findViewById(R.id.kms_semana);
        tvLitros = findViewById(R.id.ltrs_semana);
        tvPreco = findViewById(R.id.preco_semana);
        tvMedia = findViewById(R.id.resultado_media_semanal);
        rvAbastecimentos = findViewById(R.id.rv_historico_abastecimentos);
        fabAddAbastecimento = findViewById(R.id.fab_add_abastecimento);
        btnApagarVeiculo = findViewById(R.id.btn_apagar_veiculo); // <-- 3. LIGAR O BOTAO

        veiculoId = getIntent().getIntExtra("VEICULO_ID", -1);
        if (veiculoId == -1) {
            Toast.makeText(this, "Erro: ID do Veículo não encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        configurarLista();

        fabAddAbastecimento.setOnClickListener(v -> {
            Intent intent = new Intent(DetalhesVeiculoActivity.this, AdicionarAbastecimentoActivity.class);
            intent.putExtra("VEICULO_ID", veiculoId);
            startActivity(intent);
        });

        // 4. CONFIGURAR O CLIQUE DO BOTAO APAGAR
        btnApagarVeiculo.setOnClickListener(v -> {
            // Mostrar a caixa de dialogo de confirmacao
            mostrarDialogoConfirmacao();
        });

        // Mostrar a seta "Voltar"
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarDadosDoVeiculo();
    }

    private void configurarLista() {
        adapter = new AdaptadorAbastecimento(listaDeAbastecimentos);
        rvAbastecimentos.setLayoutManager(new LinearLayoutManager(this));
        rvAbastecimentos.setAdapter(adapter);
    }

    private void carregarDadosDoVeiculo() {
        databaseExecutor.execute(() -> {
            // 1. Buscar os dados do Veiculo
            // MUDANCA: Guardar o veiculo na variavel da classe
            veiculoAtual = mDb.veiculoDao().getVeiculoById(veiculoId);

            // 2. Buscar o HISTORICO de abastecimentos
            if (veiculoAtual != null) {
                listaDeAbastecimentos = mDb.abastecimentoDao().getAbastecimentosDoVeiculo(veiculoId);
            } else {
                // Se o veiculo for nulo (ex: acabou de ser apagado e deu refresh)
                listaDeAbastecimentos.clear();
            }

            // 3. Calcular os totais
            double totalKms = 0;
            double totalLitros = 0;
            double totalGasto = 0;
            for (Abastecimento ab : listaDeAbastecimentos) {
                totalKms += ab.kilometros;
                totalLitros += ab.litros;
                totalGasto += ab.custoTotal;
            }
            double mediaGeral = (totalLitros > 0) ? (totalKms / totalLitros) : 0;

            double finalTotalKms = totalKms;
            double finalTotalLitros = totalLitros;
            double finalTotalGasto = totalGasto;
            double finalMediaGeral = mediaGeral;

            // 4. Atualizar a UI na thread principal
            runOnUiThread(() -> {
                if (veiculoAtual != null) {
                    tvTitulo.setText(veiculoAtual.getMarca() + " " + veiculoAtual.getModelo());
                } else {
                    tvTitulo.setText("Veículo não encontrado");
                    // Se o veiculo foi apagado, podemos fechar este ecra
                    finish();
                    return;
                }

                tvKms.setText(String.format("Total Kms: %.1f km", finalTotalKms));
                tvLitros.setText(String.format("Total Litros: %.2f L", finalTotalLitros));
                tvPreco.setText(String.format("Total Gasto: %.2f €", finalTotalGasto));
                tvMedia.setText(String.format("Média: %.2f km/L", finalMediaGeral));
                adapter.atualizarLista(listaDeAbastecimentos);
            });
        });
    }

    // 5. ADICIONAR ESTES DOIS NOVOS METODOS

    private void mostrarDialogoConfirmacao() {
        // Se o veiculo for nulo, nao ha nada para apagar
        if (veiculoAtual == null) return;

        new AlertDialog.Builder(this)
                .setTitle("Apagar Veículo")
                .setMessage("Tem a certeza que quer apagar o veículo '" + veiculoAtual.getNome() + "'?\n\nTodos os abastecimentos associados serão apagados permanentemente.")
                .setIcon(R.drawable.ic_baseline_delete_24) // Icone de apagar (opcional)
                // Botao "Sim"
                .setPositiveButton("Sim, Apagar", (dialog, which) -> {
                    apagarVeiculo();
                })
                // Botao "Nao"
                .setNegativeButton("Não", null)
                .show();
    }

    private void apagarVeiculo() {
        if (veiculoAtual == null) return;

        // Correr o 'delete' noutra thread
        databaseExecutor.execute(() -> {
            mDb.veiculoDao().delete(veiculoAtual);

            // Voltar a thread principal para fechar o ecra
            runOnUiThread(() -> {
                Toast.makeText(this, "Veículo '" + veiculoAtual.getNome() + "' apagado.", Toast.LENGTH_SHORT).show();
                finish(); // Fecha este ecra e volta ao Painel (MainActivity)
            });
        });
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