package com.example.vtfc_master;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class IncidenciasView extends AppCompatActivity {

    private ListView listView;
    private IncidenciaAdapter incidenciaAdapter;
    private ImageButton img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incidencias_view);

        listView = findViewById(R.id.listViewIncidencias);
        img = findViewById(R.id.imageButton4);

        incidenciaAdapter = new IncidenciaAdapter(this, new ArrayList<>());
        listView.setAdapter(incidenciaAdapter);



        String url = getIntent().getStringExtra("url");
        if (url != null) {
            incidenciaAdapter.fetchIncidencias(url);
        }

        //Sirve para cerrar la sesion.
        img.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(IncidenciasView.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Toast.makeText(IncidenciasView.this, "Se ha cerrado su cuenta", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}