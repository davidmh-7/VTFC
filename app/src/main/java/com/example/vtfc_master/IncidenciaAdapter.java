package com.example.vtfc_master;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class IncidenciaAdapter extends BaseAdapter {

    private Context context;
    private List<Incidencia> incidencias;
    private ImageView imageView;

    public IncidenciaAdapter(Context context, List<Incidencia> incidencias) {
        this.context = context;
        this.incidencias = incidencias;
    }

    @Override
    public int getCount() {
        return incidencias.size();
    }

    @Override
    public Object getItem(int position) {
        return incidencias.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.card, parent, false);
        }


        TextView textViewCause = convertView.findViewById(R.id.Tipo);
        TextView textViewprovince = convertView.findViewById(R.id.Provincia);
        TextView textViewFecha= convertView.findViewById(R.id.Fecha);
        TextView textViewCarretera= convertView.findViewById(R.id.Carretera);

        Incidencia incidencia = incidencias.get(position);



        textViewCause.setText(incidencia.getCause().length() > 18 ? incidencia.getCause().substring(0, 18) + "..." : incidencia.getCause());
        textViewprovince.setText(incidencia.getProvince());
        textViewFecha.setText(incidencia.getStartDate().length() > 10 ? incidencia.getStartDate().substring(0, 10) : incidencia.getStartDate());
        textViewCarretera.setText(incidencia.getDirection());


        //Cambio el color de el marker si esta en favoritos o no
        if (incidencia.isFavorito()) {
            convertView.setBackgroundColor(Color.YELLOW);
        } else {
            convertView.setBackgroundColor(Color.WHITE);
        }

        return convertView;
    }

    public void fetchIncidencias(String urlString) {
        new FetchIncidenciasTask().execute(urlString);
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

                Log.d("JSON Response", response.toString());

                return new Gson().fromJson(response.toString(), new TypeToken<List<Incidencia>>() {}.getType());

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Incidencia> incidencias) {
            if (incidencias != null) {
                IncidenciaAdapter.this.incidencias = incidencias;
                notifyDataSetChanged();
            } else {
                Toast.makeText(context, "Error retrieving incidencias", Toast.LENGTH_SHORT).show();
            }
        }
    }
}