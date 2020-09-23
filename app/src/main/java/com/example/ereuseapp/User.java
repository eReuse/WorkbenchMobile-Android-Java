package com.example.ereuseapp;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Vector;

public class User {
    private String software;
    private String version;
    private String type;
    private String uuid;
    private Map<String, String> device;

    public User (String software, String version, String type, String uuid,
                      Map<String, String> device){
        this.software = software;
        this.version = version;
        this.type = type;
        this.uuid = uuid;
        this.device = device;
    }

    public String getSoftware() {
        return software;
    }

    public String getVersion() {
        return version;
    }

    public String getType() {
        return type;
    }

    public String getUuid() {
        return uuid;
    }

    public Map<String,String> getDevice() {
        return device;
    }
}
