package com.example.conexionconsqlserver2;

import android.content.Context;
import android.graphics.Color;
import android.os.Message;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class req {

    public static void sendPost(JSONArray jsonArray) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://cemsa2023.000webhostapp.com/insertar_tMedicion.php");
                    //URL url = new URL("https://cemsa2021.000webhostapp.com/insertar_tMedicion.php");
                    //URL url = new URL("http://192.168.0.107:8080/cemsa_1000/insertar_tMedicion.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.connect();
                    //Log.i("JSON", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(jsonArray.toString());
                    os.flush();
                    os.close();
                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , jsonArray.toString());
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public static JSONArray  buscarMediciones() {
        JSONArray jsonArray = new JSONArray();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://cemsa2023.000webhostapp.com/insertar_tMedicion.php");
                    //URL url = new URL("https://cemsa2021.000webhostapp.com/insertar_tMedicion.php");
                    //URL url = new URL("http://192.168.0.107:8080/cemsa_1000/buscar_tMedicion.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.connect();
                    /*
                    DataInputStream is = new DataInputStream(conn.getInputStream());
                    byte ar = is.readByte();
                    String arr = is.readLine();

                    jsonArray.getJSONArray(is.readByte());
                    is.close();*/

                    BufferedReader br = null;
                    if (conn.getResponseCode() == 200) {
                        br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String strCurrentLine;
                        JSONObject jsonObject = null;
                        while ((strCurrentLine = br.readLine()) != null) {
                            System.out.println(strCurrentLine);
                            jsonObject = new JSONObject(strCurrentLine);
                        }
                            } else {
                        br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                        String strCurrentLine;
                        while ((strCurrentLine = br.readLine()) != null) {
                            System.out.println(strCurrentLine);
                        }
                    }
                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , jsonArray.toString());
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    return jsonArray;
    }
}
