package com.example.conexionconsqlserver2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.widget.EditText;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OTP_Receiver extends BroadcastReceiver {
    private EditText editUltimoMensaje;
    private EditText editHoraMensaje;
    private EditText editConsolaMensaje;
    private Context context;

    public OTP_Receiver(EditText ultimoMensaje, EditText horaMensaje, EditText consolaMensaje) {
        this.editUltimoMensaje = ultimoMensaje;
        this.editHoraMensaje = horaMensaje;
        this.editConsolaMensaje = consolaMensaje;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        for (SmsMessage sms : messages) {
            String message = sms.getMessageBody();
            String otp = extractOTPFromMessage(message);
            editUltimoMensaje.setText(otp);
            insertMeasurement();
        }
    }

    private String extractOTPFromMessage(String message) {
        try {
            String[] parts = message.split("#:#");
            return parts[1].replace(".", ",");
        } catch (Exception e) {
            this.showMessageInConsole("Mensaje recibido no es de la Central");
            return "";
        }
    }

    private void insertMeasurement() {
        String measurementText = editUltimoMensaje.getText().toString();
        String[] parts = measurementText.split("-");
        if (!parts[0].isEmpty() && Integer.parseInt(parts[0]) > 0) {
            JSONArray jsonArray = new JSONArray();
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (int i = 1; i < parts.length; i++) {
                String[] measurement = parts[i].split(";");
                JSONObject measurementObject = new JSONObject();
                try {
                    measurementObject.put("med_nro", parts[0]);
                    measurementObject.put("med_ser", measurement[0]);
                    measurementObject.put("med_valor", measurement[1]);
                    measurementObject.put("med_fechaHoraSMS", format.format(new Date()));
                    measurementObject.put("med_observacion", "");
                    jsonArray.put(measurementObject);
                } catch (JSONException e) {
                    showMessageInConsole("Error JSON");
                }
            }
            req.sendPost(jsonArray);
            showMessageInConsole("Se registraron nuevas mediciones");
        }
    }

    private void showMessageInConsole(String message) {
        editConsolaMensaje.setText(message);
    }

    public void showMessageLastHour(String message) {
        editHoraMensaje.setText(message);
    }
}
