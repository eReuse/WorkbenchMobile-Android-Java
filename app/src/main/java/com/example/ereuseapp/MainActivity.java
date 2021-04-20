package com.example.ereuseapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.Call;
import retrofit2.converter.gson.GsonConverterFactory;
import android.os.Build;
import android.view.WindowManager;
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


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //public Context context = getApplicationContext();
    private Button button_snapshot;
    private Button button_read;

    private Vector<TextView> device_info = new Vector();
    private Vector<String> device_info_print = new Vector();
    private String Serial;

    String uuid = UUID.randomUUID().toString();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_snapshot = findViewById(R.id.button_snapshot);

        device_info.addElement((TextView) findViewById(R.id.uuid));
        device_info.addElement((TextView) findViewById(R.id.manufacturer));
        device_info.addElement((TextView) findViewById(R.id.model));
        device_info.addElement((TextView) findViewById(R.id.mac_address));
        device_info.addElement((TextView) findViewById(R.id.ram_size));
        device_info.addElement((TextView) findViewById(R.id.display_size));
        device_info.addElement((TextView) findViewById(R.id.data_storage));
        button_snapshot.setOnClickListener(this);

        //read phone info
        device_info_print.add("UUID: " + uuid);
        device_info_print.add("Manufacturer: " + Build.MANUFACTURER);
        device_info_print.add("Model: " + Build.MODEL);

        String macAddress = getMacAddr();
        device_info_print.add("MAC Address: " + macAddress);

        device_info_print.add("RAM Size: " + getRam() + " MB");

        device_info_print.add("Display Size: " + getDisplaySize(MainActivity.this) + " Inches");

        device_info_print.add("Data Storage: " + getStorageSize() + " GB");

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
                String typemobile = "Mobile";
                int dataStorageSize = getStorageSize();
                int displaySize = getDisplaySize(MainActivity.this); //pulgadas
                String macAddress = getMacAddr();
                uuid = UUID.randomUUID().toString();

                //mapping device
                Map<String,Object> device = new HashMap<String,Object>();
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
                /*
                JSONObject usertest = new JSONObject();
                try {
                    usertest = new JSONObject(json);
                } catch (JSONException e) {

                }
                Log.i("JSON:",json);
                */

                break;
        }
    }


    private void executeSendJSON(String json) {
        //create OkHttp client for log
        /* OkHttpClient.Builder okhttpClientBuilder = new OkHttpClient.Builder();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

        okhttpClientBuilder.addInterceptor(logging);

        logging.setLevel(HttpLoggingInterceptor.Level.BODY); */

        //build
        Retrofit.Builder builder = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())

                //.baseUrl("https://api.testing.usody.com/usodybeta/") //testing
                .baseUrl(" https://api.usody.com/usody/") //production

                /*.client(okhttpClientBuilder.build())*/;

        Retrofit retrofit = builder.build();

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

    public static int getRam() {
        Context context = ContextApp.getContext();

        ActivityManager actManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);
        long totalMemory = memInfo.totalMem;
        return (int) ((long)(totalMemory/(1024*1024)));
    }

    public static int getDisplaySize(Activity activity) {
            double x = 0, y = 0;
            int mWidthPixels, mHeightPixels;
            try {
                WindowManager windowManager = activity.getWindowManager();
                Display display = windowManager.getDefaultDisplay();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                display.getMetrics(displayMetrics);
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(display, realSize);
                mWidthPixels = realSize.x;
                mHeightPixels = realSize.y;
                DisplayMetrics dm = new DisplayMetrics();
                activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
                x = Math.pow(mWidthPixels / dm.xdpi, 2);
                y = Math.pow(mHeightPixels / dm.ydpi, 2);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return (int) Math.sqrt(x + y);
    }

    public int getStorageSize() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        long total = totalBlocks*blockSize;
        long totalGB =  total/ (1024*1024*1024);
        return (int) totalGB;
    }


}
