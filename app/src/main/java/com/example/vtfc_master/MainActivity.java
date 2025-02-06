package com.example.vtfc_master;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText editText, editText2;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editText = findViewById(R.id.editTextText1);
        editText2 = findViewById(R.id.editTextText2);
        button = findViewById(R.id.button);

        button.setOnClickListener(v -> {
            String inputCorreo = editText.getText().toString();
            String inputPassword = editText2.getText().toString();

            ApiService.login("https://apitraficopendataeuskadi.onrender.com/login", inputCorreo, inputPassword, new ApiService.LoginCallback() {
                @Override
                public void onSuccess(Map<String, Object> response) {
                    String status = (String) response.get("status");
                    if ("success".equals(status)) {
                        Intent intent = new Intent(MainActivity.this, Dashboard1.class);
                        startActivity(intent);
                    } else {
                        String message = (String) response.get("message");
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(MainActivity.this, "Usuario o contraseña incorrectos: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });

        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            // Si hay sesión activa, redirigir al Dashboard
            Intent intent = new Intent(this, Dashboard1.class);
            startActivity(intent);
            finish();
        }

    }
}