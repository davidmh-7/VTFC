package com.example.vtfc_master;

import android.os.AsyncTask;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class ApiService {
    public interface LoginCallback {
        void onSuccess(Map<String, Object> response);
        void onError(String errorMessage);
    }

    public static void login(String url, String correo, String password, LoginCallback callback) {
        new AsyncTask<Void, Void, Map<String, Object>>() {
            private String errorMessage;

            @Override
            protected Map<String, Object> doInBackground(Void... voids) {
                try {
                    URL apiUrl = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    // Crear el JSON del cuerpo
                    String jsonInput = new Gson().toJson(new LoginRequest(correo, password));
                    OutputStream os = connection.getOutputStream();
                    os.write(jsonInput.getBytes());
                    os.flush();
                    os.close();

                    // Leer la respuesta
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    reader.close();

                    // Parsear la respuesta JSON
                    return new Gson().fromJson(response.toString(), Map.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMessage = e.getMessage();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Map<String, Object> result) {
                if (result != null) {
                    callback.onSuccess(result);
                } else {
                    callback.onError(errorMessage != null ? errorMessage : "Unknown error");
                }
            }
        }.execute();
    }
}
