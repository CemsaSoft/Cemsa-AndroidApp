package com.example.conexionconsqlserver2;

import android.util.Log;
import org.json.JSONArray;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class req {

    public static void sendPost(JSONArray jsonArray) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://cemsa2023.000webhostapp.com/insertar_tMedicion.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.connect();
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(jsonArray.toString());
                    os.flush();
                    os.close();
                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG", jsonArray.toString());
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public static JSONArray buscarMediciones() {
        JSONArray jsonArray = new JSONArray();
        // Código para buscar mediciones
        return jsonArray;
    }
}
