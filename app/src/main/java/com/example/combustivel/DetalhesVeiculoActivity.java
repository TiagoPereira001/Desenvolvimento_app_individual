package com.example.combustivel;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem; // Importei para o item do menu (o botao "voltar")
import android.widget.Button; // Import da classe Button
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog; // Import para a caixa de dialogo de confirmacao
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Ecra que mostra os detalhes de um veiculo, um dos principais ecras

public class DetalhesVeiculoActivity extends AppCompatActivity {

    // Referencias para os elementos do layout XML
    private TextView tvTitulo, tvKms, tvLitros, tvPreco, tvMedia;
    private RecyclerView rvAbastecimentos;
    private FloatingActionButton fabAddAbastecimento;
    private Button btnApagarVeiculo;

    //Variáveis da Base de Dados (Room)
    private AppBaseDados mDb;

    // O ExecutorService ermite correr a base de dados numa "thread" separada.
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    //Variáveis da Lista (RecyclerView)
    private AdaptadorAbastecimento adapter;
    private List<Abastecimento> listaDeAbastecimentos = new ArrayList<>();

    // O ID do veiculo que este ecra esta a mostrar
    private int veiculoId;
    // O objeto "Veiculo" completo.
    private Veiculo veiculoAtual;

    // Metodo "OnCreate" chamamos quando o ecra é criado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Liga este ficheiro Java ao seu ficheiro de layout XML
        setContentView(R.layout.activity_detalhes_veiculo);

        //Inicia a instancia da base de dados
        mDb = AppBaseDados.getDatabase(getApplicationContext());

        //Liga as variaveis Java aos IDs dos elementos no XML
        tvTitulo = findViewById(R.id.tv_detalhe_titulo);
        tvKms = findViewById(R.id.kms_semana);
        tvLitros = findViewById(R.id.ltrs_semana);
        tvPreco = findViewById(R.id.preco_semana);
        tvMedia = findViewById(R.id.resultado_media_semanal);
        rvAbastecimentos = findViewById(R.id.rv_historico_abastecimentos);
        fabAddAbastecimento = findViewById(R.id.fab_add_abastecimento);
        btnApagarVeiculo = findViewById(R.id.btn_apagar_veiculo);

        // busca o ID do veiculo que a MainActivity nos enviou.
        // O "getIntent()" é quem transporta dados entre ecras.
        veiculoId = getIntent().getIntExtra("VEICULO_ID", -1);

        // Se o ID for -1, mostra um erro
        if (veiculoId == -1) {
            Toast.makeText(this, "Erro: ID do Veículo não encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //Chama o metodo para configurar o RecyclerView
        configurarLista();

        //Configura o clique do botao (+)
        fabAddAbastecimento.setOnClickListener(v -> {
            // Cria uma 'Intent'  para abrir o ecra "AdicionarAbastecimentoActivity"
            Intent intent = new Intent(DetalhesVeiculoActivity.this, AdicionarAbastecimentoActivity.class);
            // Envia o ID deste veiculo para esse ecra
            intent.putExtra("VEICULO_ID", veiculoId);
            startActivity(intent);
        });

        //Configura o clique do botao "Apagar"
        btnApagarVeiculo.setOnClickListener(v -> {
            // Chama o metodo que mostra a caixa de dialogo de confirmacao
            mostrarDialogoConfirmacao();
        });

        //Ativa a seta "Voltar" na barra de topo
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    // Metodo "OnResume" que é chamado quando o utilizador volta ao ecra

    @Override
    protected void onResume() {
        super.onResume();
        // Recarrega todos os dados da Base de dados
        carregarDadosDoVeiculo();
    }

    // Metodo que inicia e configura o RecyclerView
    private void configurarLista() {
        // Cria o adaptador
        adapter = new AdaptadorAbastecimento(listaDeAbastecimentos);
        // Define que a lista é vertical
        rvAbastecimentos.setLayoutManager(new LinearLayoutManager(this));
        // Liga o adaptador ao RecyclerView
        rvAbastecimentos.setAdapter(adapter);
    }

    // Metodo principal de logica, vai a base de dados, busca os dados, faz os calculos e atualiza o ecra

    private void carregarDadosDoVeiculo() {
        //Executa a logica da BD na thread separada
        databaseExecutor.execute(() -> {

            //Busca o objeto Veiculo completo
            //Guarda numa variavel da classe para usar noutros metodos
            veiculoAtual = mDb.veiculoDao().getVeiculoById(veiculoId);

            //Buscar o historico  de abastecimentos
            if (veiculoAtual != null) {
                //Se o veiculo existe, vai buscar os seus abastecimentos
                listaDeAbastecimentos = mDb.abastecimentoDao().getAbastecimentosDoVeiculo(veiculoId);
            } else {
                // Se o veiculo nao existe, limpa a lista
                listaDeAbastecimentos.clear();
            }

            //Calcula os totais (Kms, Litros, Gasto, Media)
            double totalKms = 0;
            double totalLitros = 0;
            double totalGasto = 0;
            // Repete sobre cada abastecimento na lista
            for (Abastecimento ab : listaDeAbastecimentos) {
                totalKms += ab.kilometros;
                totalLitros += ab.litros;
                totalGasto += ab.custoTotal;
            }
            // Calcula a media
            double mediaGeral = (totalLitros > 0) ? (totalKms / totalLitros) : 0;

            // Copia os totais para variaveis final
            double finalTotalKms = totalKms;
            double finalTotalLitros = totalLitros;
            double finalTotalGasto = totalGasto;
            double finalMediaGeral = mediaGeral;

            //Volta à thread principal para atualizar o ecra
            runOnUiThread(() -> {
                // Verifica se o veiculo ainda existe
                if (veiculoAtual != null) {
                    // Define o titulo do ecra
                    tvTitulo.setText(veiculoAtual.getMarca() + " " + veiculoAtual.getModelo());
                } else {
                    // Se o veiculo foi apagado, o 'veiculoAtual' é nulo
                    tvTitulo.setText("Veículo não encontrado");
                    // Fecha este ecra, porque o veiculo ja nao existe
                    finish();
                    return; // Sai do metodo

                }

                // Atualiza o "cartao" de estatisticas

                tvKms.setText(String.format("Total Kms: %.1f km", finalTotalKms));
                tvLitros.setText(String.format("Total Litros: %.2f L", finalTotalLitros));
                tvPreco.setText(String.format("Total Gasto: %.2f €", finalTotalGasto));
                tvMedia.setText(String.format("Média: %.2f km/L", finalMediaGeral));

                //Atualiza o (RecyclerView) com os novos dados
                adapter.atualizarLista(listaDeAbastecimentos);
            });
        });
    }

    // Usei isto para evitar "misclick" para confirmar com o utilizador

    private void mostrarDialogoConfirmacao() {
        // Se o veiculo for nulo, nao ha nada para apagar
        if (veiculoAtual == null) return;

        // Usa o Construtor de AlertDialog
        new AlertDialog.Builder(this)
                .setTitle("Apagar Veículo") // Titulo da caixa
                // Mensagem de confirmacao
                .setMessage("Tem a certeza que quer apagar o veículo '" + veiculoAtual.getNome() + "'?\n\nTodos os abastecimentos associados serão apagados permanentemente.")
                .setIcon(R.drawable.apagar) // Icone de apagar

                // Botao "Sim"
                .setPositiveButton("Sim, Apagar", (dialog, which) -> {
                    // Se o utilizador clicar "Sim", chama o metodo 'apagarVeiculo()'
                    apagarVeiculo();
                })

                // Botao "Nao"
                .setNegativeButton("Não", null)
                .show(); // Mostrar o dialogo
    }

    // apaga o veiculo da base de dadosm só é chamado se o utilizador confirmar o "delete"

    private void apagarVeiculo() {
        if (veiculoAtual == null) return;

        //Corre a operacao 'delete' na thread da base de dados
        databaseExecutor.execute(() -> {

            //Chama o comando @Delete do DAO,

            mDb.veiculoDao().delete(veiculoAtual);

            // Volta a UI thread para mostrar o Toast e fecha o ecra
            runOnUiThread(() -> {
                Toast.makeText(this, "Veículo '" + veiculoAtual.getNome() + "' apagado.", Toast.LENGTH_SHORT).show();
                finish(); // Fecha este ecra
            });
        });
    }


    // metodo chamado quando o utilizador clica na seta "voltar" na barra de topo

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