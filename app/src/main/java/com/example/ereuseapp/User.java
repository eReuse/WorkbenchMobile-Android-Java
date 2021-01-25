package com.example.ereuseapp;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Vector;

public class User {

    //@Expose
    private Map<String, String> device;
    private String type;
    private String uuid;
    private String software;
    private String version;


    public User (String type, Map<String, String> device, String uuid, String software, String version){
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
