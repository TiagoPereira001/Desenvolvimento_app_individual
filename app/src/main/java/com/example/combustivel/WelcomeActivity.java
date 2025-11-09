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

        //Inicia o SharedPreferences
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        //Verifica se o nome ja existe
        String nomeGuardado = prefs.getString(KEY_USER_NAME, null);
        if (nomeGuardado != null) {
            // Se o nome ja existe, salta este ecra
            goToMainActivity();
            return;
        }

        // Se o nome nao existe, mostra o layout de boas-vindas
        setContentView(R.layout.activity_welcome);

        //Liga os elementos do XML
        editNome = findViewById(R.id.edit_user_name);
        btnContinuar = findViewById(R.id.btn_continuar);

        //Configura o clique do botao
        btnContinuar.setOnClickListener(v -> {
            String nomeInserido = editNome.getText().toString().trim();

            //Valida se o nome nao esta vazio
            if (nomeInserido.isEmpty()) {
                Toast.makeText(this, "Por favor, insere o teu nome", Toast.LENGTH_SHORT).show();
            } else {
                // Guarda o nome
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(KEY_USER_NAME, nomeInserido);
                editor.apply();

                // Ir para a app principal
                goToMainActivity();
            }
        });
    }

    // metodo para inciar o ecra principal e fechar este

    private void goToMainActivity() {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}