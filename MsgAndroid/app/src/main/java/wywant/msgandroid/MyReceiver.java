package wywant.msgandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import wywant.msgandroid.MsgActivity;

public class MyReceiver extends BroadcastReceiver {
    public static final String SMS_BUNDLE = "pdus";
    public MyReceiver() {
        super();
    }

    private String encode(String message){
        try{
            String eMessage = URLEncoder.encode(message,"UTF-8");
            return eMessage;
        } catch (UnsupportedEncodingException e){
            return message;
        }
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        //http://javapapers.com/android/android-receive-sms-tutorial/
        final MsgActivity msgActivity = MsgActivity.getInstance();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(msgActivity);
        String hostname = sharedPref.getString("hostname", "");
        String port = sharedPref.getString("port","8080");
        final Resources resources = msgActivity.getResources();
        if(hostname.isEmpty()){
            msgActivity.addAMessage(resources.getString(R.string.msgHostnameIsRequired));
            return;
        }
        String urlPrefix = "http://" + hostname + ":" + port + "/xiaoxi/recieveSMS";
        Bundle intentExtras = intent.getExtras();

        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);

            String format = "3gpp";
            for (int i = 0; i < sms.length; ++i) {
//https://developer.android.com/reference/android/telephony/SmsMessage.html#createFromPdu(byte[],%20java.lang.String)
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i], format);

                String smsBody = smsMessage.getMessageBody().toString();

                String address = smsMessage.getOriginatingAddress();
                String eAddress = encode(address);
                String eSmsBody = encode(smsBody);
                String url = urlPrefix + "?a=" + eAddress + "&c=" + eSmsBody;

                msgActivity.addAMessage(resources.getString(R.string.msgRecieveASMS) + address);



//https://developer.android.com/training/volley/requestqueue.html#network
                final RequestQueue queue = Volley.newRequestQueue(context);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Do something with the response
                                msgActivity.addAMessage(response);
                                queue.stop();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // Handle error
                                //Log.e("A",error.getMessage());
                                msgActivity.addAMessage(resources.getString(R.string.msgFailToSendToServer));
                                queue.stop();
                            }
                        });
                queue.add(stringRequest);

            }
           // Toast.makeText(context, smsMessageStr, Toast.LENGTH_SHORT).show();

            //this will update the UI with message
           // SmsActivity inst = SmsActivity.instance();
           // inst.updateList(smsMessageStr);
        }
    }
}
