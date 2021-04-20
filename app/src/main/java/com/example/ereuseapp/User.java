package com.example.ereuseapp;

import java.util.Map;

public class User {

    //@Expose
    private Map<String, Object> device;
    private String type;
    private String uuid;
    private String software;
    private String version;


    public User (String type, Map<String, Object> device, String uuid, String software, String version){
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

    public Map<String,Object> getDevice() {
        return device;
    }

}
