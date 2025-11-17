package com.example.combustivel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class WelcomeActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private TextInputEditText editNome;
    private Button btnContinuar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(ModoActivity.PREFS_NAME, MODE_PRIVATE);

        if (prefs.getString(ModoActivity.KEY_USER_NAME, null) != null) {
            goToMainActivity();
            return;
        }

        setContentView(R.layout.activity_welcome);
        editNome = findViewById(R.id.edit_user_name);
        btnContinuar = findViewById(R.id.btn_continuar);

        btnContinuar.setOnClickListener(v -> {
            String nomeInserido = editNome.getText().toString().trim();
            if (nomeInserido.isEmpty()) {
                Toast.makeText(this, "Por favor, insere o teu nome", Toast.LENGTH_SHORT).show();
            } else {
                prefs.edit().putString(ModoActivity.KEY_USER_NAME, nomeInserido).apply();
                goToMainActivity();
            }
        });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}