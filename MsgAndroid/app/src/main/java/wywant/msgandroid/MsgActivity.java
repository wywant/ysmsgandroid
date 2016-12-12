package wywant.msgandroid;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MsgActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    private ArrayList<String> dataList;

    private static MsgActivity instance;

    public static MsgActivity getInstance(){
        return instance;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        //https://developer.android.com/reference/android/widget/Button.html

       // this.getSharedPreferences("aaa", Context.MODE_PRIVATE).getString("server","server:port");
        //EditTextPreference aa = new EditTextPreference(this);

        ListView list = (ListView)findViewById(R.id.contentListView);

        dataList = new ArrayList<>();


        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, android.R.id.text1, dataList);
        list.setAdapter(adapter);

        addAMessage(getResources().getString(R.string.msgAppStarted));

    }

    @Override
    protected void onStart() {
        super.onStart();
        instance = this;
    }

    public void checkPermissions(MenuItem menuItem){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS,Manifest.permission.RECEIVE_SMS},2);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        } else {
            showMessage(R.string.msgPermissionOKMessage,R.string.msgPermissionOKTitle);
        }
    }
    public void changeHostPort(MenuItem menuItem){


        DialogFragment newFragment = new FireMissilesDialogFragment();
        newFragment.show(getSupportFragmentManager(), "missiles");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void showMessage(int message,int title){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle(title);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void connectionTest(MenuItem menuItem){
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String hostname = sharedPref.getString("hostname", "");
        String port = sharedPref.getString("port","8080");

        if(hostname.isEmpty()){
            showMessage(R.string.messageHostIsRequiredMessage,R.string.messageHostIsRequiredTitle);
            return;
        }
        final RequestQueue queue = Volley.newRequestQueue(this);
        String testURL = "http://" + hostname + ":" + port + "/xiaoxi/connectionTest";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, testURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do something with the response
                        queue.stop();
                     //   Log.e("A",response);
                        showMessage(R.string.connectionTestOKMessage,R.string.connectionTestOKTile);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                     //   Log.e("A",error.getMessage());
                        queue.stop();
                        showMessage(R.string.connectionTestFailMessage,R.string.connectionTestFailTile);
                    }
                });
        queue.add(stringRequest);




    }

    void addAMessage(String message){
        if(dataList.size() > 10){
            dataList.remove(0);
        }
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
        String time = format.format(new Date());
        dataList.add(time + "  " + message);
        adapter.notifyDataSetChanged();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }
}
