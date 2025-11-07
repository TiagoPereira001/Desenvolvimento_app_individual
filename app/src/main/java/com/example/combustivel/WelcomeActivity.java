package com.example.combustivel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class WelcomeActivity extends AppCompatActivity {

    // Variavel para guardar o nome do utilizador
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "com.example.combustivel.PREFS";
    private static final String KEY_USER_NAME = "USER_NAME";

    private TextInputEditText editNome;
    private Button btnContinuar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Iniciar o SharedPreferences
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // 2. Verificar se o nome JA EXISTE
        String nomeGuardado = prefs.getString(KEY_USER_NAME, null);
        if (nomeGuardado != null) {
            // Se o nome ja existe, saltamos este ecra
            goToMainActivity();
            return; // Importante para parar a execucao aqui
        }

        // 3. Se o nome NAO existe, mostramos o layout de boas-vindas
        setContentView(R.layout.activity_welcome);

        // 4. Ligar os elementos do XML
        editNome = findViewById(R.id.edit_user_name);
        btnContinuar = findViewById(R.id.btn_continuar);

        // 5. Configurar o clique do botao
        btnContinuar.setOnClickListener(v -> {
            String nomeInserido = editNome.getText().toString().trim();

            // Validar se o nome nao esta vazio
            if (nomeInserido.isEmpty()) {
                Toast.makeText(this, "Por favor, insere o teu nome", Toast.LENGTH_SHORT).show();
            } else {
                // Guardar o nome
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(KEY_USER_NAME, nomeInserido);
                editor.apply();

                // Ir para a app principal
                goToMainActivity();
            }
        });
    }

    /**
     * Metodo que inicia o ecra principal (MainActivity) e fecha este.
     */
    private void goToMainActivity() {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Fecha o WelcomeActivity para o utilizador nao voltar
    }
}