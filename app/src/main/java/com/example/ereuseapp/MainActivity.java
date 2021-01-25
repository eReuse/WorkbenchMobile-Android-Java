package com.example.ereuseapp;

import android.app.ActivityManager;
import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
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


import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //public Context context = getApplicationContext();
    private Button button_snapshot;
    private Button button_read;

    private Vector<TextView> device_info = new Vector();
    private Vector<String> device_info_print = new Vector();
    private String Serial;

    final String uuid = UUID.randomUUID().toString();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_snapshot = findViewById(R.id.button_snapshot);

        device_info.addElement((TextView) findViewById(R.id.uuid));
        device_info.addElement((TextView) findViewById(R.id.manufacturer));
        device_info.addElement((TextView) findViewById(R.id.model));
        device_info.addElement((TextView) findViewById(R.id.req_sent));
        device_info.addElement((TextView) findViewById(R.id.mac_address));

        button_snapshot.setOnClickListener(this);

        //read phone info
        device_info_print.add("UUID: " + uuid);
        device_info_print.add("Manufacturer: " + Build.MANUFACTURER);
        device_info_print.add("Model: " + Build.MODEL);

        String macAddress = getMacAddr();
        device_info_print.add("MAC Adress: " + macAddress);

        //device_info_print.add("Serial Number: " );
        device_info_print.add("RAM Size: " + getRam()+ " GB");

        device_info_print.add("Display Size: " + getScreenWidth() +"x"+ getScreenHeight());
        device_info_print.add("dataStorageSize: Undefined" );

        for (int i = 0; i < device_info.size(); ++i)
            if (device_info.get(i) != null)
                device_info.get(i).setText(device_info_print.get(i));


//        Log.d("getDeviceName", Integer.toString(device_info.size()));
    }



    @Override
    public void onClick(View view){
        switch (view.getId()) {
            //taking snapshot button
            case R.id.button_snapshot:

                ////////values of JSON//////////
                String software = "WorkbenchAndroid";
                String version = "0.0.2";
                String type = "Snapshot";

                //dummy values
                String typemobile = "Mobile";
                //String manufacturer = "Samsung";
                //String model = "gt-19505";
                //String serialNumber = "serialNumber";
                //String ramSize = "2048";
                String dataStorageSize = "16384";
                String displaySize = "5";
                String macAddress = getMacAddr();

                //mapping device
                Map<String,String> device = new HashMap<String,String>();
                device.put("serialNumber",macAddress);
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
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Data sent!", Toast.LENGTH_LONG).show();
                }
                else {
                    try {
                        Toast.makeText(MainActivity.this, "Server returned error: " + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this, String.format("Not able to connect to server", Toast.LENGTH_LONG),
                        Toast.LENGTH_LONG).show();
            }
        });
    }



    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif: all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b: macBytes) {
                    //res1.append(Integer.toHexString(b & 0xFF) + ":");
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {}
        return "02:00:00:00:00:00";
    }

    public static String getRam() {
        Context context = ContextApp.getContext();

        ActivityManager actManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);
        long totalMemory = memInfo.totalMem;
        return Long.toString(totalMemory/(1024*1024));
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

}
