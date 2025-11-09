package com.example.combustivel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// esté é o ecra principal

public class MainActivity extends AppCompatActivity {

    //Variáveis da Interface
    // A lista (RecyclerView) que  mostra os "cartões" dos veículos
    private RecyclerView rvVeiculos;
    // O botão (+) no canto
    private FloatingActionButton fabAddVeiculo;
    // O layout que aparece quando a garagem está vazia
    private LinearLayout emptyStateLayout;
    // O TextView que vai mostrar "Olá, "Tiago"
    private TextView tvGreeting;

    // Variáveis da Base de Dados
    // A nossa instância da (Room)
    private AppBaseDados mDb;
    // O ExecutorService ermite correr a base de dados numa "thread" separada.
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    //Variáveis do Adaptador da Lista
    // O nosso adaptador
    private AdaptadorVeiculo adapter;
    // A lista de dados para o adaptador
    private List<Veiculo> listaDeVeiculos = new ArrayList<>();

    // Metodo "OnCreate" chamamos quando o ecra é criado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Liga o ficheiro Java ao ficheiro de layout
        setContentView(R.layout.painel_activity);

        //Obtem a instância da base de dados
        mDb = AppBaseDados.getDatabase(getApplicationContext());

        //Liga todas as  variáveis da interface aos IDs do XML
        rvVeiculos = findViewById(R.id.rv_veiculos);
        fabAddVeiculo = findViewById(R.id.fab_add_veiculo);
        emptyStateLayout = findViewById(R.id.empty_state_layout);
        tvGreeting = findViewById(R.id.tv_greeting);

        // 4. Chama o metodo  para carregar e mostrar o nome do utilizador
        mostrarSaudacao();

        // 5. Chamar o metodo que prepara o RecyclerView
        configurarLista();

        //Configura o clique do botão  (+)
        fabAddVeiculo.setOnClickListener(v -> {
            // Cria uma Intent para abrir o ecrã "AdicionarVeiculoActivity"
            Intent intent = new Intent(MainActivity.this, AdicionarVeiculoActivity.class);
            //Executar a Intent
            startActivity(intent);
        });
    }

    // Metodo que le o nome do utilizador que foi guardado no "WelcomeActivity" e mostra na tela

    private void mostrarSaudacao() {
        // Acede ao ficheiro de SharedPreferences
        SharedPreferences prefs = getSharedPreferences("com.example.combustivel.PREFS", MODE_PRIVATE);

        //Le a string guardada na chave "USER_NAME".
        // Se não encontrar nada usa "Utilizador" como valor "default".
        String nome = prefs.getString("USER_NAME", "Utilizador");

        //Definir o texto no TextView com a saudação.
        tvGreeting.setText("Olá, " + nome + "!");
    }


    // Metodo "OnResume" que é chamado quando o utilizador volta ao ecra

    @Override
    protected void onResume() {
        super.onResume();
        // Recarrega todos os dados da Base de dados
        carregarVeiculosDaBD();
    }

    // Metodo que prepara o RecyclerView

    private void configurarLista() {
        // Cria uma nova Instance do nosso "AdaptadorVeiculo".
        //Passa a lista de dados e a lógica de clique.
        adapter = new AdaptadorVeiculo(listaDeVeiculos, veiculo -> {
            // Esta é a implementação da interface OnVeiculoClickListener

            // Cria uma Intent para abrir o ecrã de Detalhes
            Intent intent = new Intent(MainActivity.this, DetalhesVeiculoActivity.class);

            // Envia o ID do veículo que foi clicado para o próximo ecrã.
            intent.putExtra("VEICULO_ID", veiculo.getId());
            startActivity(intent);
        });

        // Dize ao RecyclerView como organizar os itens.
        rvVeiculos.setLayoutManager(new LinearLayoutManager(this));

        //Liga o RecyclerView ao nosso adaptador
        rvVeiculos.setAdapter(adapter);
    }

    // Carrega a lista da base de dados numa thread separada

    private void carregarVeiculosDaBD() {
        // Executa a operação da BD na no background
        databaseExecutor.execute(() -> {

            // Chama o DAO para  buscar todos os veículos da tabela.
            listaDeVeiculos = mDb.veiculoDao().getAllVeiculos();

            // Depois de buscar os dados, volta à thread principal para atualizar
            runOnUiThread(() -> {

                // Entrega a nova lista ao adaptador.
                adapter.atualizarLista(listaDeVeiculos);

                //Lógica do "Estado Vazio"
                //Verifica se a lista que veio da BD está vazia.
                if (listaDeVeiculos.isEmpty()) {
                    // Se estiver vazia: Esconde a lista e mostra um aviso
                    rvVeiculos.setVisibility(View.GONE);
                    emptyStateLayout.setVisibility(View.VISIBLE);
                } else {
                    // Se tiver itens: Mostra a lista e esconde o aviso
                    rvVeiculos.setVisibility(View.VISIBLE);
                    emptyStateLayout.setVisibility(View.GONE);
                }
            });
        });
    }
}