package com.example.combustivel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetalhesVeiculoActivity extends AppCompatActivity implements AdaptadorAbastecimento.OnAbastecimentoLongClickListener {

    private TextView tvTitulo, tvKms, tvLitros, tvPreco, tvMedia;
    private RecyclerView rvAbastecimentos;
    private FloatingActionButton fabAddAbastecimento;
    private Button btnApagarVeiculo, btnEstimativa;
    private LinearLayout layoutRangeCalculator;
    private TextInputEditText editPercentagemBateria;
    private Button btnCalcularRange;
    private TextView tvResultadoRange;
    private BarChart barChart;

    private AppBaseDados mDb;
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    private AdaptadorAbastecimento adapter;
    private List<Abastecimento> listaDeAbastecimentos = new ArrayList<>();

    private int veiculoId;
    private Veiculo veiculoAtual;
    private double mediaCalculada = 0;

    private boolean isPro = false;
    private SharedPreferences prefs;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_veiculo);

        prefs = getSharedPreferences(ModoActivity.PREFS_NAME, MODE_PRIVATE);
        isPro = prefs.getBoolean(ModoActivity.KEY_IS_PRO_USER, false);
        mDb = AppBaseDados.getDatabase(getApplicationContext());

        // Ligar variaveis
        tvTitulo = findViewById(R.id.tv_detalhe_titulo);
        tvKms = findViewById(R.id.kms_semana);
        tvLitros = findViewById(R.id.ltrs_semana);
        tvPreco = findViewById(R.id.preco_semana);
        tvMedia = findViewById(R.id.resultado_media_semanal);
        rvAbastecimentos = findViewById(R.id.rv_historico_abastecimentos);
        fabAddAbastecimento = findViewById(R.id.fab_add_abastecimento);
        btnApagarVeiculo = findViewById(R.id.btn_apagar_veiculo);
        btnEstimativa = findViewById(R.id.btn_estimativa);
        layoutRangeCalculator = findViewById(R.id.layout_range_calculator);
        editPercentagemBateria = findViewById(R.id.edit_percentagem_bateria);
        btnCalcularRange = findViewById(R.id.btn_calcular_range);
        tvResultadoRange = findViewById(R.id.tv_resultado_range);
        barChart = findViewById(R.id.bar_chart);
        adView = findViewById(R.id.adView_detalhes);

        veiculoId = getIntent().getIntExtra("VEICULO_ID", -1);
        if (veiculoId == -1) {
            Toast.makeText(this, "Erro: ID do Veículo não encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        configurarLista();
        configurarAnuncios();

        fabAddAbastecimento.setOnClickListener(v -> {
            Intent intent = new Intent(DetalhesVeiculoActivity.this, AdicionarAbastecimentoActivity.class);
            intent.putExtra("VEICULO_ID", veiculoId);
            startActivity(intent);
        });

        btnApagarVeiculo.setOnClickListener(v -> mostrarDialogoConfirmacao());

        btnEstimativa.setOnClickListener(v -> {
            if (mediaCalculada == 0) {
                Toast.makeText(this, "Sem dados suficientes para estimar.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (isPro) {
                Intent intent = new Intent(DetalhesVeiculoActivity.this, EstimativaActivity.class);
                intent.putExtra("VEICULO_ID", veiculoId);
                intent.putExtra("VEICULO_TIPO", veiculoAtual.getTipoVeiculo());
                intent.putExtra("VEICULO_MEDIA", mediaCalculada);
                startActivity(intent);
            } else {
                mostrarPopupPro("A estimativa de consumo de viagem é uma funcionalidade PRO.\n\nDeseja comprar?");
            }
        });

        btnCalcularRange.setOnClickListener(v -> calcularRange());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void configurarAnuncios() {
        if (!isPro) {
            MobileAds.initialize(this, initializationStatus -> {});
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        } else {
            adView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarDadosDoVeiculo();
    }

    private void configurarLista() {
        String tipo = (veiculoAtual != null) ? veiculoAtual.getTipoVeiculo() : "COMBUSTAO";
        adapter = new AdaptadorAbastecimento(listaDeAbastecimentos, tipo, this);
        rvAbastecimentos.setLayoutManager(new LinearLayoutManager(this));
        rvAbastecimentos.setAdapter(adapter);
    }

    private void carregarDadosDoVeiculo() {
        databaseExecutor.execute(() -> {
            veiculoAtual = mDb.veiculoDao().getVeiculoById(veiculoId);

            if (veiculoAtual != null) {
                listaDeAbastecimentos = mDb.abastecimentoDao().getAbastecimentosDoVeiculo(veiculoId);
            } else {
                listaDeAbastecimentos.clear();
            }

            double totalKms = 0, totalUnidades = 0, totalGasto = 0;
            for (Abastecimento ab : listaDeAbastecimentos) {
                totalKms += ab.kilometros;
                totalUnidades += ab.litros;
                totalGasto += ab.custoTotal;
            }

            mediaCalculada = (totalKms > 0) ? (totalUnidades / totalKms) * 100 : 0;
            double finalTotalKms = totalKms, finalTotalUnidades = totalUnidades, finalTotalGasto = totalGasto, finalMediaGeral = mediaCalculada;

            runOnUiThread(() -> {
                if (veiculoAtual == null) {
                    finish();
                    return;
                }

                String tipo = veiculoAtual.getTipoVeiculo();
                tvTitulo.setText(veiculoAtual.getMarca() + " " + veiculoAtual.getModelo());
                tvKms.setText(String.format(Locale.getDefault(), "Total Kms: %.1f km", finalTotalKms));
                tvPreco.setText(String.format(Locale.getDefault(), "Total Gasto: %.2f €", finalTotalGasto));

                if (tipo.equals("ELETRICO")) {
                    tvLitros.setText(String.format(Locale.getDefault(), "Total kWh: %.1f kWh", finalTotalUnidades));
                    tvMedia.setText(String.format(Locale.getDefault(), "Média: %.2f kWh/100km", finalMediaGeral));
                    btnEstimativa.setVisibility(View.VISIBLE);
                    layoutRangeCalculator.setVisibility(View.VISIBLE);
                    tvResultadoRange.setText("");
                } else {
                    tvLitros.setText(String.format(Locale.getDefault(), "Total Litros: %.1f L", finalTotalUnidades));
                    tvMedia.setText(String.format(Locale.getDefault(), "Média: %.2f L/100km", finalMediaGeral));
                    btnEstimativa.setVisibility(View.GONE);
                    layoutRangeCalculator.setVisibility(View.GONE);
                }

                adapter = new AdaptadorAbastecimento(listaDeAbastecimentos, tipo, this);
                rvAbastecimentos.setAdapter(adapter);
                configurarGrafico(listaDeAbastecimentos);
            });
        });
    }

    private void calcularRange() {
        String percentagemTexto = editPercentagemBateria.getText().toString();
        if (veiculoAtual == null || mediaCalculada == 0) { return; }
        if (percentagemTexto.isEmpty()) {
            Toast.makeText(this, "Insira a percentagem da bateria", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            double percentagem = Double.parseDouble(percentagemTexto);
            if (percentagem < 0 || percentagem > 100) {
                Toast.makeText(this, "Insira um valor entre 0 e 100", Toast.LENGTH_SHORT).show();
                return;
            }
            double capacidadeTotal = veiculoAtual.getCapacidadeBateria();
            double kWhDisponiveis = capacidadeTotal * (percentagem / 100.0);
            double media = mediaCalculada;
            double autonomiaEstimada = (kWhDisponiveis / media) * 100.0;
            tvResultadoRange.setText(String.format(Locale.getDefault(), "~ %.0f km", autonomiaEstimada));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Valor de percentagem inválido", Toast.LENGTH_SHORT).show();
        }
    }

    private void configurarGrafico(List<Abastecimento> abastecimentos) {
        Map<String, Float> gastosPorMes = new HashMap<>();
        List<String> labelsMeses = new ArrayList<>();
        SimpleDateFormat formatadorMesAno = new SimpleDateFormat("MMM/yy", Locale.getDefault());
        Calendar cal = Calendar.getInstance();

        for (Abastecimento ab : abastecimentos) {
            cal.setTimeInMillis(ab.data);
            String mesAno = formatadorMesAno.format(cal.getTime());
            float totalAtual = gastosPorMes.getOrDefault(mesAno, 0f);
            totalAtual += (float) ab.custoTotal;
            gastosPorMes.put(mesAno, totalAtual);
            if (!labelsMeses.contains(mesAno)) {
                labelsMeses.add(mesAno);
            }
        }

        ArrayList<BarEntry> entradasGrafico = new ArrayList<>();
        for (int i = 0; i < labelsMeses.size(); i++) {
            String mes = labelsMeses.get(i);
            float gasto = gastosPorMes.get(mes);
            entradasGrafico.add(new BarEntry(i, gasto));
        }

        if (entradasGrafico.isEmpty()) {
            barChart.clear();
            barChart.invalidate();
            return;
        }

        BarDataSet dataSet = new BarDataSet(entradasGrafico, "Gastos por Mês");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);
        BarData barData = new BarData(dataSet);

        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setText("Euros (€)");
        barChart.animateY(1000);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labelsMeses));
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setGranularityEnabled(true);
        barChart.getXAxis().setLabelRotationAngle(-45);
        barChart.invalidate();
    }

    // --- Metodos Pro ---
    private void mostrarPopupPro(String mensagem) {
        new AlertDialog.Builder(this)
                .setTitle("Funcionalidade PRO")
                .setMessage(mensagem)
                .setPositiveButton("Sim, comprar", (dialog, which) -> simularCompraPro())
                .setNegativeButton("Agora Não", null)
                .show();
    }

    private void simularCompraPro() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ModoActivity.KEY_IS_PRO_USER, true);
        editor.apply();
        this.isPro = true;
        Toast.makeText(this, "Compra PRO simulada com sucesso! Tente clicar no botão outra vez.", Toast.LENGTH_LONG).show();
        configurarAnuncios(); // Esconder o anuncio
    }

    // --- Metodos Editar/Apagar Abastecimento ---
    @Override
    public void onAbastecimentoLongClicked(Abastecimento abastecimento) {
        final CharSequence[] items = {"Editar Registo", "Apagar Registo"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Escolha uma Ação");
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals("Editar Registo")) {
                Intent intent = new Intent(this, AdicionarAbastecimentoActivity.class);
                intent.putExtra("VEICULO_ID", veiculoId);
                intent.putExtra("EXTRA_ABASTECIMENTO_ID", abastecimento.id);
                startActivity(intent);
            } else if (items[item].equals("Apagar Registo")) {
                mostrarDialogoApagarAbastecimento(abastecimento);
            }
        });
        builder.show();
    }

    private void mostrarDialogoApagarAbastecimento(Abastecimento abastecimento) {
        new AlertDialog.Builder(this)
                .setTitle("Apagar Registo")
                .setMessage("Tem a certeza que quer apagar este registo?")
                .setPositiveButton("Sim, Apagar", (dialog, which) -> apagarRegisto(abastecimento))
                .setNegativeButton("Não", null)
                .show();
    }

    private void apagarRegisto(Abastecimento abastecimento) {
        databaseExecutor.execute(() -> {
            mDb.abastecimentoDao().delete(abastecimento);
            runOnUiThread(this::carregarDadosDoVeiculo);
        });
        Toast.makeText(this, "Registo apagado", Toast.LENGTH_SHORT).show();
    }

    // --- Metodos Apagar Veiculo ---
    private void mostrarDialogoConfirmacao() {
        if (veiculoAtual == null) return;
        new AlertDialog.Builder(this)
                .setTitle("Apagar Veículo")
                .setMessage("Tem a certeza que quer apagar o veículo '" + veiculoAtual.getNome() + "'?\n\nTodos os registos associados serão apagados permanentemente.")
                .setIcon(R.drawable.apagar)
                .setPositiveButton("Sim, Apagar", (dialog, which) -> apagarVeiculo())
                .setNegativeButton("Não", null)
                .show();
    }

    private void apagarVeiculo() {
        if (veiculoAtual == null) return;
        databaseExecutor.execute(() -> {
            mDb.veiculoDao().delete(veiculoAtual);
            runOnUiThread(() -> {
                Toast.makeText(this, "Veículo '" + veiculoAtual.getNome() + "' apagado.", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    // --- Metodo Botao Voltar (Barra) ---
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}