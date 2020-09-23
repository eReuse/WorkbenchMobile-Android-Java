package com.example.ereuseapp;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiManager {

    private static IMobileApi service;
    private static ApiManager apiManager;

    private ApiManager() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.dh.usody.net/dbtest/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(IMobileApi.class);
    }

    public static ApiManager getInstance() {
        if (apiManager == null) {
            apiManager = new ApiManager();
        }
        return apiManager;
    }

    public void sendUser(JSONObject user, Callback<JSONObject> callback) {
        Call<JSONObject> userCall = service.sendUser(user);
        userCall.enqueue(callback);
    }
}