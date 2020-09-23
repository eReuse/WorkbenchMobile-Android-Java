package com.example.ereuseapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import 	android.app.ActivityManager.MemoryInfo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;

import org.json.JSONObject;
import org.json.JSONException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.net.HttpURLConnection;
import java.lang.Object;
import java.time.LocalDateTime;

public class MainActivity extends AppCompatActivity {

    private Vector<TextView> device_info = new Vector();
    private Vector<String> device_info_print = new Vector();
    private String Serial;
    static final int MY_PERMISSIONS_REQUEST_INTERNET = 130;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        device_info.addElement((TextView) findViewById(R.id.manufacturer));
        device_info.addElement((TextView) findViewById(R.id.model));
        device_info.addElement((TextView) findViewById(R.id.req_sent));

//        Log.d("getDeviceName", Integer.toString(device_info.size()));
    }

    public void getDeviceName(View view) {
        int request_code = 1;
        device_info_print.add("Manufacturer: " + Build.MANUFACTURER);
        device_info_print.add("Model: " + Build.MODEL);
         /* ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_PHONE_STATE},
                request_code);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
            if ( Build.VERSION.SDK_INT >= 26) Serial = Build.getSerial();
            else; //buscar serial api pre 26
        else Serial = "Undefined";*/

        //get internet access
        int internet_permission = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        switch (internet_permission) {
            case PackageManager.PERMISSION_GRANTED:
                device_info_print.add("JSON sent: True");
                sendSnapshot(view);
                break;
            default:
                ActivityCompat.requestPermissions(
                        this, new String[]{Manifest.permission.INTERNET}, MY_PERMISSIONS_REQUEST_INTERNET);
                //device_info_print.add("JSON sent: False");
        }

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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_INTERNET: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission granted

                } else {
                    //permission denied
                    device_info_print.add("JSON sent: Denied");
                }
                return;
            }
            //other cases
        }
    }

    private void sendSnapshot(View view) {

        String url = "https://api.dh.usody.net/dbtest/actions/";
        JSONObject jsonObject = null;

        //values of JSON
        String software = "WorkbenchAndroid";
        String version = "0.0.1";
        String type = "Snapshot";
        String uuid = "24a335a1-d400-4111-a413-ef79017156e1";

        //device map values
        String typemobile = "Mobile";
        String manufacturer = "Samsung";
        String model = "gt-19505";
        String serialNumber = "serialNumber";

        //mapping device
        Map<String,String> device = new HashMap<String,String>();
        device.put("serialNumber",serialNumber);
        device.put("model",model);
        device.put("manufacturer",manufacturer);
        device.put("type",typemobile);

        User snapshotUser = new User(software, version, type, uuid, device);

        //create Json
        Gson gsonObj = new Gson();
        String json = gsonObj.toJson(snapshotUser);
        JSONObject user = new JSONObject();
        try {
            user = new JSONObject(json);
        } catch (JSONException e) {

        }

        //Log.i("Current JSON",json);

        MainApplication.apiManager.sendUser(user, new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                JSONObject responseUser = response.body();
                if (response.isSuccessful() && responseUser != null) {
                    Toast.makeText(MainActivity.this,
                            String.format("Data sent"),
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, String.format ("Response is %s", String.valueOf(response.code())), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                    Toast.makeText(MainActivity.this,
                            "Error is " + t.getMessage()
                            , Toast.LENGTH_LONG).show();
            }
        });
        //extra possible values
         /*   "type: Mobile"
            "manufacturer:" + Build.MANUFACTURER
            "model:" + Build.MODEL
            "serialNumber:" +
            "displaySize:" +
            "ramSize:" +
            "dataStorageSize:" +
            "imei: " +
            "meid: " +
         */
    }
}
