package com.example.ereuseapp;

import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.ereuseapp.R;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.Call;
import retrofit2.converter.gson.GsonConverterFactory;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import 	android.app.ActivityManager.MemoryInfo;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.app.Activity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;

import org.json.JSONObject;
import org.json.JSONException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.net.HttpURLConnection;
import java.lang.Object;
import java.time.LocalDateTime;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //public Context context = getApplicationContext();
    private Button button_snapshot;
    private Button button_read;

    private Vector<TextView> device_info = new Vector();
    private Vector<String> device_info_print = new Vector();
    private String Serial;
    static final int MY_PERMISSIONS_REQUEST_INTERNET = 130;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_snapshot = findViewById(R.id.button_snapshot);
        button_read = findViewById(R.id.button_read);

        device_info.addElement((TextView) findViewById(R.id.manufacturer));
        device_info.addElement((TextView) findViewById(R.id.model));
        device_info.addElement((TextView) findViewById(R.id.req_sent));

        button_snapshot.setOnClickListener(this);
        button_read.setOnClickListener(this);

//        Log.d("getDeviceName", Integer.toString(device_info.size()));
    }

    @Override
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.button_read:

                device_info_print.add("Manufacturer: " + Build.MANUFACTURER);
                device_info_print.add("Model: " + Build.MODEL);
                //int request_code = 1;
                 /* ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        request_code);
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
                    if ( Build.VERSION.SDK_INT >= 26) Serial = Build.getSerial();
                    else; //buscar serial api pre 26
                else Serial = "Undefined";*/

                //get internet access
                /*int internet_permission = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
                switch (internet_permission) {
                    case PackageManager.PERMISSION_GRANTED:
                        device_info_print.add("JSON made: True");
                        break;
                    default:
                        ActivityCompat.requestPermissions(
                                this, new String[]{Manifest.permission.INTERNET}, MY_PERMISSIONS_REQUEST_INTERNET);
                        //device_info_print.add("JSON made: False");
                }*/

                //get info
                device_info_print.add("Serial Number: " + Serial);
                device_info_print.add("Display Size: Undefined" /*get info from android studio */ );
                device_info_print.add("RAM Size: Undefined" /*PARAM FROM ACTIVITY MANAGER */);
                device_info_print.add("dataStorageSize: Undefined" );
                device_info_print.add("IMEI: Undefined");
                device_info_print.add("MEID: Undefined");

                for (int i = 0; i < device_info.size(); ++i)
                    if (device_info.get(i) != null)
                        device_info.get(i).setText(device_info_print.get(i));
                break;

            case R.id.button_snapshot:
                //values of JSON
                String software = "WorkbenchAndroid";
                String version = "0.0.1";
                String type = "Snapshot";
                String uuid = "240035a1-d400-4111-a413-ef79017156e1";

                //device map values
                String typemobile = "Mobile";
                String manufacturer = "Samsung";
                String model = "gt-19505";
                String serialNumber = "serialNumber";
                String ramSize = "2048";
                String dataStorageSize = "16384";
                String displaySize = "5";


                //mapping device
                Map<String,String> device = new HashMap<String,String>();
                device.put("serialNumber",serialNumber);
                device.put("model",model);
                device.put("manufacturer",manufacturer);
                device.put("type",typemobile);
                device.put("ramSize",ramSize);
                device.put("dataStorageSize",dataStorageSize);
                device.put("displaySize",displaySize);

                User user = new User(type, device, uuid, software, version);

                //create Json
                Gson gsonObj = new Gson();
                String json = gsonObj.toJson(user);
                executeSendJSON(json);
                //Testing JSON format
              /*JSONObject usertest = new JSONObject();
                try {
                    usertest = new JSONObject(json);
                } catch (JSONException e) {

                }
                Log.i("Current JSON",json);*/

//////////////////////////////////////////////////////////////
                /*MainApplication.apiManager.sendUser(user, new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.body() != null) {
                            User responseUser = response.body();
                            if (response.isSuccessful() && responseUser != null) {
                                Toast.makeText(MainActivity.this, "Device added to database", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(MainActivity.this, String.format("Response is %s", String.valueOf(response.code())),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Error" + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });*/

                break;
        }
    }


    private void executeSendJSON(String json) {

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.testing.usody.com/usodybeta/")
                .build();
        AppClient appClient = retrofit.create(AppClient.class);
        Call<Void> call = appClient.sendJson(json);
        //get response
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(MainActivity.this, "Connection to database successful", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this, String.format("Response is wrong", Toast.LENGTH_LONG),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

}
