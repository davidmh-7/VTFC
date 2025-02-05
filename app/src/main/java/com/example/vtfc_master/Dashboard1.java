package com.example.vtfc_master;

import static com.example.vtfc_master.db.IncidenciasBBDD.COLUMN_FAVORITO;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vtfc_master.R;
import com.example.vtfc_master.db.CamaraBBDD;
import com.example.vtfc_master.db.IncidenciasBBDD;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Dashboard1 extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mapView;
    private GoogleMap googleMap;
    private IncidenciaAdapter incidenciaAdapter;
    private ListView listView;
    private TextView textView;
    private ImageView imagen;
    private Camara camara;
    private ImageButton img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard1);

        listView = findViewById(R.id.listViewIncidencias);


        incidenciaAdapter = new IncidenciaAdapter(this, new ArrayList<>());
        listView.setAdapter(incidenciaAdapter);

        //Llamo a la api
        incidenciaAdapter.fetchIncidencias("https://apitraficopendataeuskadi.onrender.com/incidencia");

        mapView = findViewById(R.id.mapView2);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        textView = findViewById(R.id.textView8);
        imagen = findViewById(R.id.flechaIncidencias);

        img = findViewById(R.id.imageButton4);

        //Abre la vista para inicidencias
        textView.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard1.this, IncidenciasView.class);
            intent.putExtra("url", "https://apitraficopendataeuskadi.onrender.com/incidencia");
            startActivity(intent);
        });

        imagen.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard1.this, IncidenciasView.class);
            intent.putExtra("url", "https://apitraficopendataeuskadi.onrender.com/incidencia");
            startActivity(intent);
        });

        //Sirve para cerrar la sesion.
        img.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(Dashboard1.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Toast.makeText(Dashboard1.this, "Se ha cerrado su cuenta", Toast.LENGTH_SHORT).show();
            finish();
        });


        //Gestion de camaras
        new FetchCamarasTask().execute("https://apitraficopendataeuskadi.onrender.com/camaras");
        new FetchIncidenciasTask().execute("https://apitraficopendataeuskadi.onrender.com/incidencia");

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        LatLng specificLocation = new LatLng(43.300407, -1.960076);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(specificLocation, 10));

        // Cargar cámaras e incidencias desde la base de datos local
        cargarMarcadoresDesdeBBDD();

        mostrarInfoCamsIncis();
    }

    private void cargarMarcadoresDesdeBBDD() {
        // Cargar cámaras
        CamaraBBDD camaraBBDD = new CamaraBBDD(this);
        List<Camara> camaras = camaraBBDD.obtenerTodasCamaras();
        for (Camara camara : camaras) {
            double latitude = Double.parseDouble(camara.getLatitude());
            double longitude = Double.parseDouble(camara.getLongitude());
            LatLng location = new LatLng(latitude, longitude);

            float color = camara.isFavoritoCam() ? BitmapDescriptorFactory.HUE_YELLOW : BitmapDescriptorFactory.HUE_RED;
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(camara.getCameraName())
                    .icon(BitmapDescriptorFactory.defaultMarker(color)));
            marker.setTag(camara);
        }

        // Cargar incidencias
        IncidenciasBBDD incidenciasBBDD = new IncidenciasBBDD(this);
        List<Incidencia> incidencias = incidenciasBBDD.obterTodasIncidencias();
        for (Incidencia incidencia : incidencias) {
            double latitude = Double.parseDouble(incidencia.getLatitude());
            double longitude = Double.parseDouble(incidencia.getLongitude());
            LatLng location = new LatLng(latitude, longitude);

            float color = incidencia.isFavorito() ? BitmapDescriptorFactory.HUE_GREEN : BitmapDescriptorFactory.HUE_BLUE;
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(incidencia.getIncidenceName())
                    .snippet(incidencia.getCause())
                    .icon(BitmapDescriptorFactory.defaultMarker(color)));
            marker.setTag(incidencia);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    private class FetchCamarasTask extends AsyncTask<String, Void, List<Camara>> {

        @Override
        protected List<Camara> doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                Log.d("JSON Response", response.toString());

                return new Gson().fromJson(response.toString(), new TypeToken<List<Camara>>() {}.getType());

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Camara> camaras) {
            if (camaras != null && googleMap != null) {
                for (Camara camara : camaras) {
                    double latitude = Double.parseDouble(camara.getLatitude());
                    double longitude = Double.parseDouble(camara.getLongitude());
                    LatLng location = new LatLng(latitude, longitude);

                    // Verificar si es favorito
                    float color = camara.isFavoritoCam() ? BitmapDescriptorFactory.HUE_YELLOW : BitmapDescriptorFactory.HUE_RED;

                    Marker marker = googleMap.addMarker(new MarkerOptions()
                            .position(location)
                            .title(camara.getCameraName())
                            .icon(BitmapDescriptorFactory.defaultMarker(color))); // Asignar color
                    marker.setTag(camara);  // Añadir el tag
                }
            } else {
                Toast.makeText(Dashboard1.this, "Error retrieving cameras or map not ready", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class FetchIncidenciasTask extends AsyncTask<String, Void, List<Incidencia>> {

        @Override
        protected List<Incidencia> doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                Log.d("JSON Response Incidencias", response.toString());

                return new Gson().fromJson(response.toString(), new TypeToken<List<Incidencia>>() {
                }.getType());

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Incidencia> incidencias) {
            if (incidencias != null && googleMap != null) {
                for (Incidencia incidencia : incidencias) {
                    double latitude = Double.parseDouble(incidencia.getLatitude());
                    double longitude = Double.parseDouble(incidencia.getLongitude());
                    LatLng location = new LatLng(latitude, longitude);

                    // Verificar si es favorito
                    float color = incidencia.isFavorito() ? BitmapDescriptorFactory.HUE_GREEN : BitmapDescriptorFactory.HUE_BLUE;

                    Marker marker = googleMap.addMarker(new MarkerOptions()
                            .position(location)
                            .title(incidencia.getIncidenceName())
                            .snippet(incidencia.getCause())
                            .icon(BitmapDescriptorFactory.defaultMarker(color))); // Asignar color
                    marker.setTag(incidencia);  // Añadir el tag
                }
            } else {
                Toast.makeText(Dashboard1.this, "Error retrieving incidencias or map not ready", Toast.LENGTH_SHORT).show();
            }
        }

    }


    public void mostrarInfoCamsIncis() {
        googleMap.setOnMarkerClickListener(marker -> {
            mostrarDialogo(marker);
            return true;
        });
    }

    public void mostrarDialogo(Marker marker) {
        Object tag = marker.getTag();

        Dialog dialog = new Dialog(Dashboard1.this);
        dialog.setContentView(R.layout.info_ventana);

        TextView titulo = dialog.findViewById(R.id.textViewTitulo);
        TextView descripcion = dialog.findViewById(R.id.textViewDesc);
        ImageButton favoritoBtn = dialog.findViewById(R.id.imagenStar);
        ImageView imagenObjeto = dialog.findViewById(R.id.imagenObjeto);
        TextView textoblanco = dialog.findViewById(R.id.textoblanco);
        TextView textoblanco2 = dialog.findViewById(R.id.textoblanco2);
        TextView textoblanco3 = dialog.findViewById(R.id.textoblanco3);

        if (tag instanceof Camara) {
            Camara camara = (Camara) tag;
            titulo.setText(camara.getCameraName());
            descripcion.setText(camara.getRoad());
            String url = camara.getUrlImage().replace("http://", "https://");
            Picasso.get().load(url).into(imagenObjeto);
            Log.d("mostrarDialogo", "URL: " + url);

            boolean esFavorito = camara.isFavoritoCam();
            favoritoBtn.setImageResource(esFavorito ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);

            favoritoBtn.setOnClickListener(v -> {
                boolean nuevoEstadoFavorito = !camara.isFavoritoCam();
                camara.setFavoritoCam(nuevoEstadoFavorito);
                favoritoBtn.setImageResource(nuevoEstadoFavorito ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);

                // Actualizar en la base de datos
                CamaraBBDD dbHelper = new CamaraBBDD(Dashboard1.this);
                dbHelper.toggleFavorito(camara.getCameraId(), nuevoEstadoFavorito);

                // Actualizar el color del marcador
                float color = nuevoEstadoFavorito ? BitmapDescriptorFactory.HUE_YELLOW : BitmapDescriptorFactory.HUE_RED;
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(color));
            });

        } else if (tag instanceof Incidencia) {
            Incidencia incidencia = (Incidencia) tag;
            titulo.setText(incidencia.getIncidenceName());
            descripcion.setText(incidencia.getCityTown());
            textoblanco.setText(incidencia.getCause());
            textoblanco2.setText(incidencia.getPkStart());
            textoblanco3.setText(incidencia.getEndDate());
            boolean esFavorito = incidencia.isFavorito();



            favoritoBtn.setImageResource(esFavorito ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);

            favoritoBtn.setOnClickListener(v -> {
                boolean nuevoEstadoFavorito = !incidencia.isFavorito();
                incidencia.setFavorito(nuevoEstadoFavorito);
                favoritoBtn.setImageResource(nuevoEstadoFavorito ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);

                // Actualizar en la base de datos
                IncidenciasBBDD dbHelper = new IncidenciasBBDD(Dashboard1.this);
                dbHelper.toggleFavorito(incidencia.getIncidenceId(), nuevoEstadoFavorito);

                // Actualizar el color del marcador
                float color = nuevoEstadoFavorito ? BitmapDescriptorFactory.HUE_GREEN : BitmapDescriptorFactory.HUE_BLUE;
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(color));
            });
        }

        dialog.show();
    }
}