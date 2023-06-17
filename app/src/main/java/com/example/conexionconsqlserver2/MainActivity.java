package com.example.conexionconsqlserver2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private EditText edtMensaje, edtHoraMensaje, edtConsolaMensaje;
    private TableLayout listaM;
    private RequestQueue requestQueue;

    private static final String URL = "https://cemsa2023.000webhostapp.com/buscar_tMedicion.php";
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestSMSPermission();

        edtMensaje = findViewById(R.id.edtMensaje);
        edtHoraMensaje = findViewById(R.id.edtHoraMensaje);
        edtConsolaMensaje = findViewById(R.id.edtConsolaMensaje);
        listaM = findViewById(R.id.lista);

        buscarMedicion(URL);
        new OTP_Receiver().setEditText(edtMensaje,edtHoraMensaje,edtConsolaMensaje);
/*
        Timer timer = new Timer();
        TimerTask tarea = new TimerTask() {
            @Override
            public void run() {
                buscarMedicion(URL);
            }
        };
        timer.schedule(tarea, 100, 60000);
*/
        Button btnActualizarMedicion = findViewById(R.id.btnActualizarMedicion);
        btnActualizarMedicion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //buscarMedicion(URL);
                listarMensajesNoLeidos();
            }
        });

        //listarMensajesSMS();
        //listarMensajesNoLeidos();
    }

    private void requestSMSPermission() {
        String permission = Manifest.permission.RECEIVE_SMS;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permissionList = {permission};
            ActivityCompat.requestPermissions(this, permissionList, PERMISSION_REQUEST_CODE);
        }
    }

    private void buscarMedicion(String url) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    TableLayout lista = findViewById(R.id.lista);
                    lista.removeAllViews();

                    String[] headers = {"Med id", "Med Nro", "Med Ser", "Med Valor", "Fecha Hora Reg"};
                    lista.addView(insertarValorTabla(headers, Color.WHITE, R.color.colorPrimary));

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String[] values = {
                                jsonObject.getString("med_id"),
                                jsonObject.getString("med_nro"),
                                jsonObject.getString("med_ser"),
                                jsonObject.getString("med_valor"),
                                jsonObject.getString("med_fechaHoraSMS")
                        };
                        lista.addView(insertarValorTabla(values, Color.BLACK, R.color.colorVerdeAgua));
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "ERROR de Conexión", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private TableRow insertarValorTabla(String[] values, int colorLetra, int colorFondo) {
        TableRow row = new TableRow(getBaseContext());
        for (int i = 0; i < values.length; i++) {
            TextView textView = new TextView(getBaseContext());
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setPadding(10, 15, 10, 15);
            textView.setBackgroundResource(colorFondo);
            textView.setText(values[i]);
            textView.setTextColor(colorLetra);
            row.addView(textView);
        }
        return row;
    }

    private void listarMensajesSMS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            try {
                Uri uri = Uri.parse("content://sms/inbox");
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        String address = cursor.getString(cursor.getColumnIndex("address"));
                        String body = cursor.getString(cursor.getColumnIndex("body"));
                        // Aquí puedes mostrar o utilizar los datos de los mensajes SMS como desees
                        Log.d("SMS", "Address: " + address + ", Body: " + body);
                    } while (cursor.moveToNext());
                    cursor.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Si no tienes permiso para leer los mensajes SMS, solicítalo
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, PERMISSION_REQUEST_CODE);
        }
    }
    private void listarMensajesNoLeidos() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            try {
                Uri uri = Uri.parse("content://sms/inbox");
                Cursor cursor = getContentResolver().query(uri, null, "read = 0", null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        String address = cursor.getString(cursor.getColumnIndex("address"));
                        String body = cursor.getString(cursor.getColumnIndex("body"));
                        long timestamp = cursor.getLong(cursor.getColumnIndex("date"));

                        // Obtener la hora del mensaje
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(timestamp);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String horaMensaje = dateFormat.format(calendar.getTime());

                        // Aquí puedes mostrar o utilizar los datos de los mensajes SMS no leídos como desees
                        Log.d("SMS", "Address: " + address + ", Body: " + body + ", Timestamp: " + horaMensaje);
                    } while (cursor.moveToNext());
                    cursor.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Si no tienes permiso para leer los mensajes SMS, solicítalo
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, PERMISSION_REQUEST_CODE);
        }
    }


}
