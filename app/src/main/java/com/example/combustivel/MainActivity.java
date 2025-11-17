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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvVeiculos;
    private FloatingActionButton fabAddVeiculo;
    private LinearLayout emptyStateLayout;
    private TextView tvGreeting;

    private AppBaseDados mDb;
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    private AdaptadorVeiculo adapter;
    private List<Veiculo> listaDeVeiculos = new ArrayList<>();

    private String appMode;
    private boolean isPro = false;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.painel_activity);

        mDb = AppBaseDados.getDatabase(getApplicationContext());

        SharedPreferences prefs = getSharedPreferences(ModoActivity.PREFS_NAME, MODE_PRIVATE);
        appMode = prefs.getString(ModoActivity.KEY_APP_MODE, "COMBUSTAO");
        isPro = prefs.getBoolean(ModoActivity.KEY_IS_PRO_USER, false);

        // Ligar variaveis
        rvVeiculos = findViewById(R.id.rv_veiculos);
        fabAddVeiculo = findViewById(R.id.fab_add_veiculo);
        emptyStateLayout = findViewById(R.id.empty_state_layout);
        tvGreeting = findViewById(R.id.tv_greeting);
        adView = findViewById(R.id.adView);

        mostrarSaudacao(prefs);
        configurarLista();
        configurarAnuncios();

        fabAddVeiculo.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AdicionarVeiculoActivity.class);
            startActivity(intent);
        });
    }

    private void mostrarSaudacao(SharedPreferences prefs) {
        String nome = prefs.getString(ModoActivity.KEY_USER_NAME, "Utilizador");
        tvGreeting.setText("Olá, " + nome + "!");
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
            listaDeVeiculos.clear();
            if (appMode.equals("AMBOS")) {
                listaDeVeiculos = mDb.veiculoDao().getAllVeiculos();
            } else {
                listaDeVeiculos = mDb.veiculoDao().getVeiculosDeTipo(appMode);
            }

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