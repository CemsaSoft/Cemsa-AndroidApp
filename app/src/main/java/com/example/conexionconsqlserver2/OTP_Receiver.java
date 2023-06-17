package com.example.conexionconsqlserver2;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.EditText;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OTP_Receiver extends BroadcastReceiver {
    private static EditText editUltimoMensaje;
    private static EditText editHoraMensaje;
    private static EditText editConsolaMensaje;
    public static Date fecha_msn;

    public void setEditText(EditText ultimoMensaje, EditText horaMensaje, EditText consolaMensaje) {
        editUltimoMensaje = ultimoMensaje;
        editHoraMensaje = horaMensaje;
        editConsolaMensaje = consolaMensaje;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context context, Intent intent) {
        SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        for (SmsMessage sms : messages) {
            String message = sms.getMessageBody();
            fecha_msn = new Date(sms.getTimestampMillis());
            String otp;
            try {
                otp = message.replace(".", ",");
                otp = message.split("#:#")[1];
            } catch (Exception e) {
                otp = "";
                mensajeConsola("Mensaje recibido no es de la Central");
            }
            editUltimoMensaje.setText(otp);
            insertarMedicion();

            // Marcar el mensaje como leído
            marcarMensajeComoLeido(context, sms);
        }
    }

    public void mensajeConsola(String mensaje) {
        editConsolaMensaje.setText(mensaje);
    }

    public void mensajeUltimaHora(String mensaje) {
        editHoraMensaje.setText(mensaje);
    }

    private void insertarMedicion() {
        String[] parts, medicion;
        parts = editUltimoMensaje.getText().toString().split("-");
        if (!parts[0].isEmpty() && Integer.parseInt(parts[0]) > 0) {
            JSONArray jsonArray = new JSONArray();
            try {
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for (int i = 1; i < parts.length; i++) {
                    JSONObject jsonObject = new JSONObject();
                    medicion = parts[i].split(";");
                    jsonObject.put("med_nro", parts[0]);
                    jsonObject.put("med_ser", medicion[0]);
                    jsonObject.put("med_valor", medicion[1]);
                    jsonObject.put("med_fechaHoraSMS", format.format(fecha_msn));
                    mensajeUltimaHora(format.format(fecha_msn));
                    jsonObject.put("med_observacion", "");
                    jsonArray.put(jsonObject);
                }
                sendPost(jsonArray);
                mensajeConsola("Se registraron nuevas mediciones");
            } catch (JSONException e) {
                mensajeConsola("Error Json...");
            }
        }
    }

    private void marcarMensajeComoLeido(Context context, SmsMessage sms) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Uri messageUri = Uri.parse("content://sms/inbox");
                ContentResolver contentResolver = context.getContentResolver();

                ContentValues values = new ContentValues();
                values.put("read", 1); // Marcar como leído

                String where = "address=? AND date=?";
                String[] selectionArgs = new String[]{sms.getOriginatingAddress(), String.valueOf(sms.getTimestampMillis())};

                contentResolver.update(messageUri, values, where, selectionArgs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private static void sendPost(JSONArray jsonArray) {
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
}
