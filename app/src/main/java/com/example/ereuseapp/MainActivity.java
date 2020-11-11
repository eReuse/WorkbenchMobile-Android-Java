package com.example.ereuseapp;

import android.app.ActivityManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;


import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.Call;
import retrofit2.converter.gson.GsonConverterFactory;
import android.os.Build;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;



import retrofit2.Callback;
import retrofit2.converter.scalars.ScalarsConverterFactory;


import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //public Context context = getApplicationContext();
    private Button button_snapshot;
    private Button button_read;

    private Vector<TextView> device_info = new Vector();
    private Vector<String> device_info_print = new Vector();
    private String Serial;



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

                //display info
                device_info_print.add("Manufacturer: " + Build.MANUFACTURER);
                device_info_print.add("Model: " + Build.MODEL);
                device_info_print.add("Serial Number: " + "Unknown"/*Build.getSerial()*/);
                device_info_print.add("RAM Size: " + getRam());
                device_info_print.add("IMEI: " + getIMEI());

                device_info_print.add("MEID: Undefined");
                device_info_print.add("Display Size: Undefined" /*get info from android studio */ );
                device_info_print.add("dataStorageSize: Undefined" );

                for (int i = 0; i < device_info.size(); ++i)
                    if (device_info.get(i) != null)
                        device_info.get(i).setText(device_info_print.get(i));
                break;

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

            case R.id.button_snapshot:

                Random rand = new Random();

                ////////values of JSON//////////
                String software = "WorkbenchAndroid";
                String version = "0.0.1";
                String type = "Snapshot";
                String uuid = String.valueOf(rand.nextInt(1000000));

                //dummy values
                String typemobile = "Mobile";
                //String manufacturer = "Samsung";
                //String model = "gt-19505";
                String serialNumber = "serialNumber";
                //String ramSize = "2048";
                String dataStorageSize = "16384";
                String displaySize = "5";

                //mapping device
                Map<String,String> device = new HashMap<String,String>();
                device.put("serialNumber",serialNumber);
                device.put("model",Build.MODEL);
                device.put("manufacturer",Build.MANUFACTURER);
                device.put("type",typemobile);
                device.put("ramSize",getRam());
                device.put("dataStorageSize",dataStorageSize);
                device.put("displaySize",displaySize);

                User user = new User(type, device, uuid, software, version);

                //create Json
                Gson gsonObj = new Gson();
                String json = gsonObj.toJson(user);

                //send JSON to the interface
                executeSendJSON(json);

                //Testing JSON format
              /*JSONObject usertest = new JSONObject();
                try {
                    usertest = new JSONObject(json);
                } catch (JSONException e) {

                }
                Log.i("Current JSON",json);*/

                break;
        }
    }


    private void executeSendJSON(String json) {

        //build
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

    public static String getRam() {
        Context context = ContextApp.getContext();

        ActivityManager actManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);
        long totalMemory = memInfo.totalMem;
        return Long.toString(totalMemory);
    }

    public String getIMEI(){
        Context context = ContextApp.getContext();
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }
}
