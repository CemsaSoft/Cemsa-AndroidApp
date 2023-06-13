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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    String texto="";
    EditText edtHoraMensaje, edtConsolaMensaje;
    EditText edtMensaje;
    Button btnAgregar;
    Button btnBuscar;
    TableLayout listaM;
    RequestQueue requestQueue;
    String[] parts, medicion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestSMSPermission();

        edtMensaje=(EditText)findViewById(R.id.edtMensaje);
        edtHoraMensaje=(EditText)findViewById(R.id.edtHoraMensaje);
        edtConsolaMensaje=(EditText)findViewById(R.id.edtConsolaMensaje);
        listaM=(TableLayout)findViewById(R.id.lista);

        buscarMedicion("https://cemsa2023.000webhostapp.com/buscar_tMedicion.php");
        //buscarMedicion("https://cemsa2021.000webhostapp.com/buscar_tMedicion.php");
        new OTP_Receiver().setEditText(edtMensaje,edtHoraMensaje,edtConsolaMensaje);

        Timer timer = new Timer();
        TimerTask tarea = new TimerTask() {
            @Override
            public void run() {
                //buscarMedicion("https://cemsa2021.000webhostapp.com/buscar_tMedicion.php");
                buscarMedicion("https://cemsa2023.000webhostapp.com/buscar_tMedicion.php");

            }
        };
        timer.schedule(tarea, 100 , 60000);

        Button btnAgregar=(Button)findViewById(R.id.btnActualizarMedicion);
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //buscarMedicion("https://cemsa2021.000webhostapp.com/buscar_tMedicion.php");
                buscarMedicion("https://cemsa2023.000webhostapp.com/buscar_tMedicion.php");

            }
        });
    }

    /* This function will be called by the broadcast receiver */
    private void requestSMSPermission()
    {
        String permission = Manifest.permission.RECEIVE_SMS;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED)
        {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(this, permission_list,1);
        }
    }
    private void buscarMedicion(String URL){
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                TableLayout lista = (TableLayout) findViewById(R.id.lista);
                //borro la lista
                lista.removeAllViews();
                String [] cadenaE={"Med id","Med Nro","Med Ser","Med Valor","Fecha Hora Reg"};
                lista.addView(insertarValorTabla(cadenaE,Color.WHITE,R.color.colorPrimary));

                for (int ii = 0; ii < response.length(); ii++) {
                    try {
                        jsonObject = response.getJSONObject(ii);
                        String [] cadena={jsonObject.getString("med_id"),jsonObject.getString("med_nro"),jsonObject.getString("med_ser"),jsonObject.getString("med_valor"),jsonObject.getString("med_fechaHoraSMS")};
                        lista.addView(insertarValorTabla(cadena,Color.BLACK,R.color.colorVerdeAgua));

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "ERROR de Conexión", Toast.LENGTH_SHORT).show();
            }
        }
        );
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    public void agregarRegTabla(JSONArray response){
        JSONObject jsonObject = null;
        TableLayout lista = (TableLayout) findViewById(R.id.lista);
        //borro la lista
        lista.removeAllViews();
        String [] cadenaE={"Med id","Med Nro","Med Ser","Med Valor","Fecha Hora Reg"};
        lista.addView(insertarValorTabla(cadenaE,Color.WHITE,R.color.colorPrimary));

        for (int ii = 0; ii < response.length(); ii++) {
            try {
                jsonObject = response.getJSONObject(ii);
                String [] cadena={jsonObject.getString("med_id"),jsonObject.getString("med_nro"),jsonObject.getString("med_ser"),jsonObject.getString("med_valor"),jsonObject.getString("med_fechaHoraRegistro")};
                lista.addView(insertarValorTabla(cadena,Color.BLACK,R.color.colorVerdeAgua));

            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public TableRow insertarValorTabla(String[] cadena, Integer colorLetra, Integer colorFondo){
        TextView textView;
        //abrimos el table row agregar las filas
        TableRow row=new TableRow(getBaseContext());
        for(int i=0;i<5;i++){
            //abrimos un constructor del textview haciendo referencia a este proyecto
            textView = new TextView(getBaseContext());
            //para centrar el texto
            textView.setGravity(Gravity.CENTER_VERTICAL);
            //le damos dimenciones al textview
            textView.setPadding(10, 15, 10, 15);
            //un color de fondo
            textView.setBackgroundResource(colorFondo);
            //agregamos los datos del vector cadena uno por uno al textview
            textView.setText(cadena[i]);
            //color de texto en el textview
            textView.setTextColor(colorLetra);
            //agregamos el textview al TableRow
            row.addView(textView);
        }
        return row;
    }
}