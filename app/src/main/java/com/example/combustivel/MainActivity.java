package com.example.combustivel;

import android.content.Intent;
import android.content.SharedPreferences; // <-- 1. IMPORTAR
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView; // <-- 2. IMPORTAR TEXTVIEW
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvVeiculos;
    private FloatingActionButton fabAddVeiculo;
    private AppBaseDados mDb;
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    private AdaptadorVeiculo adapter;
    private List<Veiculo> listaDeVeiculos = new ArrayList<>();

    private LinearLayout emptyStateLayout;

    // --- MUDANÇA AQUI ---
    private TextView tvGreeting; // Variavel para a saudacao
    // --- FIM DA MUDANÇA ---

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.painel_activity);

        mDb = AppBaseDados.getDatabase(getApplicationContext());

        // Ligar variaveis ao XML
        rvVeiculos = findViewById(R.id.rv_veiculos);
        fabAddVeiculo = findViewById(R.id.fab_add_veiculo);
        emptyStateLayout = findViewById(R.id.empty_state_layout);

        // --- MUDANÇA AQUI ---
        // 1. Ligar o TextView da saudacao
        tvGreeting = findViewById(R.id.tv_greeting);

        // 2. Carregar e mostrar o nome do utilizador
        mostrarSaudacao();
        // --- FIM DA MUDANÇA ---

        configurarLista();

        fabAddVeiculo.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AdicionarVeiculoActivity.class);
            startActivity(intent);
        });
    }

    // --- MUDANÇA AQUI ---
    /**
     * Metodo para ler o nome do SharedPreferences e
     * definir o texto da saudacao.
     */
    private void mostrarSaudacao() {
        // Usar os mesmos nomes que definimos no WelcomeActivity
        SharedPreferences prefs = getSharedPreferences("com.example.combustivel.PREFS", MODE_PRIVATE);
        // Se nao encontrar o nome, usa "Utilizador" como default
        String nome = prefs.getString("USER_NAME", "Utilizador");

        // Definir o texto
        tvGreeting.setText("Olá, " + nome + "!");
    }
    // --- FIM DA MUDANÇA ---


    @Override
    protected void onResume() {
        super.onResume();
        carregarVeiculosDaBD();
    }

    private void configurarLista() {
        adapter = new AdaptadorVeiculo(listaDeVeiculos, veiculo -> {
            Intent intent = new Intent(MainActivity.this, DetalhesVeiculoActivity.class);
            intent.putExtra("VEICULO_ID", veiculo.getId());
            startActivity(intent);
        });

        rvVeiculos.setLayoutManager(new LinearLayoutManager(this));
        rvVeiculos.setAdapter(adapter);
    }

    private void carregarVeiculosDaBD() {
        databaseExecutor.execute(() -> {
            listaDeVeiculos = mDb.veiculoDao().getAllVeiculos();

            runOnUiThread(() -> {
                adapter.atualizarLista(listaDeVeiculos);

                if (listaDeVeiculos.isEmpty()) {
                    rvVeiculos.setVisibility(View.GONE);
                    emptyStateLayout.setVisibility(View.VISIBLE);
                } else {
                    rvVeiculos.setVisibility(View.VISIBLE);
                    emptyStateLayout.setVisibility(View.GONE);
                }
            });
        });
    }
}