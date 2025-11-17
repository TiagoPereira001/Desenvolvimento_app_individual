package com.example.combustivel;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Locale;

public class EstimativaActivity extends AppCompatActivity {

    private double mediaVeiculo;
    private String tipoVeiculo;

    private TextView tvMediaAtual;
    private TextInputEditText editDistancia;
    private Button btnCalcular;
    private CardView cardResultado;
    private TextView tvResultadoEstimativa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimativa);

        mediaVeiculo = getIntent().getDoubleExtra("VEICULO_MEDIA", 0);
        tipoVeiculo = getIntent().getStringExtra("VEICULO_TIPO");

        if (tipoVeiculo == null || mediaVeiculo == 0) {
            Toast.makeText(this, "Erro: Média de consumo não encontrada", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvMediaAtual = findViewById(R.id.tv_media_atual);
        editDistancia = findViewById(R.id.edit_distancia);
        btnCalcular = findViewById(R.id.btn_calcular_estimativa);
        cardResultado = findViewById(R.id.card_resultado);
        tvResultadoEstimativa = findViewById(R.id.tv_resultado_estimativa);

        String unidade = tipoVeiculo.equals("ELETRICO") ? "kWh/100km" : "L/100km";
        tvMediaAtual.setText(String.format(Locale.getDefault(), "Média atual: %.2f %s", mediaVeiculo, unidade));

        btnCalcular.setOnClickListener(v -> calcularEstimativa());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void calcularEstimativa() {
        String distanciaTexto = editDistancia.getText().toString();
        if (distanciaTexto.isEmpty()) {
            Toast.makeText(this, "Por favor, insira uma distância", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double distancia = Double.parseDouble(distanciaTexto);
            double gastoEstimado = (mediaVeiculo / 100) * distancia;
            String unidade = tipoVeiculo.equals("ELETRICO") ? "kWh" : "L";
            tvResultadoEstimativa.setText(String.format(Locale.getDefault(), "%.2f %s", gastoEstimado, unidade));
            cardResultado.setVisibility(View.VISIBLE);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Distância inválida", Toast.LENGTH_SHORT).show();
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