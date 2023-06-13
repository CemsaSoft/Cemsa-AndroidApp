package com.example.conexionconsqlserver2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.widget.EditText;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import net.sourceforge.jtds.jdbc.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class OTP_Receiver extends BroadcastReceiver
{
    private static EditText editUltimoMensaje;
    private static EditText editHoraMensaje;
    private static EditText editConsolaMensaje;
    public static Date fecha_msn;
    public static TableLayout lista;
    private Context baseContext;

    public void setEditText(EditText ultimoMensaje, EditText horaMensaje, EditText consolaMensaje)
    {
        OTP_Receiver.editUltimoMensaje = ultimoMensaje;
        OTP_Receiver.editHoraMensaje = horaMensaje;
        OTP_Receiver.editConsolaMensaje = consolaMensaje;
    }


    @Override
    public void onReceive(Context context, Intent intent)
    {
        SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        for (SmsMessage sms : messages)
        {
            String message = sms.getMessageBody();
            fecha_msn = new Date(sms.getTimestampMillis());
            String otp;
            try {
                otp = message.replace(".",",");
                otp = message.split("#:#")[1];
            }catch (Exception e) {
                otp="";
                this.mensajeConsola("Mensaje recibido no es de la Central");
            }
            editUltimoMensaje.setText(otp);
            this.insertarMedicion();
        }
    }

    public void mensajeConsola(String mensaje)
    {
        editConsolaMensaje.setText(mensaje);
    }
    public void mensajeUltimaHora(String mensaje)
    {
        editHoraMensaje.setText(mensaje);
    }

    private void insertarMedicion()
    {
        String[] parts, medicion;
        parts = editUltimoMensaje.getText().toString().split("-");
        if (parts[0].isEmpty() == false && parseInt(parts[0]) >0)
        {
            JSONArray jsonArray =new JSONArray();
            try {
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for (int i = 1; i < parts.length; i++) {
                    JSONObject jsonObject = new JSONObject();
                    medicion = parts[i].split(";");
                    //jsonObject.put("med_cta", parts[0]);
                    jsonObject.put("med_nro", parts[0]);
                    jsonObject.put("med_ser", medicion[0]);
                    jsonObject.put("med_valor", medicion[1]);
                    jsonObject.put("med_fechaHoraSMS", format.format(OTP_Receiver.fecha_msn));
                    this.mensajeUltimaHora(format.format(OTP_Receiver.fecha_msn));
                    jsonObject.put("med_observacion", "");
                    jsonArray.put(jsonObject);
                }
                req.sendPost(jsonArray);
                this.mensajeConsola("Se registro nuevas mediciones");
                //actualizarLista();
            } catch (JSONException e) {
               this.mensajeConsola("Error Json...");
            }
        }
    }

}