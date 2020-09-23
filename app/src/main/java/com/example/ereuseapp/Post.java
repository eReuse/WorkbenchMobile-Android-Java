package com.example.ereuseapp;

import java.util.UUID;

public class Post {
    private String type = "Snapshot";
    private String uuid = "null";
    private String software = "WorkbenchAndroid";
    private String version = "0.0.1";
    //device
    private String type_device = "Mobile";
    private String manufacturer = "null";
    private String model = "null";
    private String serialNumber = "null";
    private String displaySize = "null";
    private String ramSize = "null";
    private String dataStorageSize = "null";
    private String imei = "null";

    public String getUUID() {
        uuid = UUID.randomUUID().toString();
        return uuid;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getManufacturer() {
        return manufacturer;
    }


    public void setModel(String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
    }


    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setDisplaySize(String displaySize) {
        this.displaySize = displaySize;
    }

    public String getDisplaySize() {
        return displaySize;
    }

    public void setRamSize(String ramSize) {
        this.ramSize = ramSize;
    }

    public String getRamSize() {
        return ramSize;
    }

    public void setDataStorageSize(String dataStorageSize) {
        this.dataStorageSize = dataStorageSize;
    }

    public String getDataStorageSize() {
        return dataStorageSize;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getImei() {
        return imei;
    }
}
