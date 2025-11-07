package com.example.combustivel;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Este e o ecra principal (Painel/Dashboard), o LAUNCHER
public class MainActivity extends AppCompatActivity {

    private RecyclerView rvVeiculos;
    private FloatingActionButton fabAddVeiculo;
    private AppBaseDados mDb;
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    private AdaptadorVeiculo adapter;
    private List<Veiculo> listaDeVeiculos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ligar ao layout painel_activity.xml
        setContentView(R.layout.painel_activity);

        mDb = AppBaseDados.getDatabase(getApplicationContext());

        rvVeiculos = findViewById(R.id.rv_veiculos);
        fabAddVeiculo = findViewById(R.id.fab_add_veiculo);

        configurarLista();

        fabAddVeiculo.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AdicionarVeiculoActivity.class);
            startActivity(intent);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarVeiculosDaBD();
    }

    private void configurarLista() {
        adapter = new AdaptadorVeiculo(listaDeVeiculos, veiculo -> {
            // Ao clicar num veiculo, abrir os Detalhes
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
            });
        });
    }
}