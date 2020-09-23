package com.example.ereuseapp;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface IMobileApi {

        @Headers({
                "Authorization: Basic ODdiZmUwNDYtOTEyMC00MzMwLWE4ODctMzNmNGFjNTFmMjYzOg==",
                "Content-Type: application/json"
        })
        @POST("actions")
        Call<JSONObject> sendUser(@Body JSONObject user);

}
