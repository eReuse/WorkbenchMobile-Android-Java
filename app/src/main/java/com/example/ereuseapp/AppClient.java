package com.example.ereuseapp;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface AppClient {

    @Headers({
            "Authorization: Basic ODY5ODRlZTgtYTdjOC00ZjdiLWE1NWYtYWMyNzdmYTlmMjQxOg==",
//                "Content-Type: application/json"
    })
    @POST("actions/")
    Call<Void> sendJson(@Body String message);

        //Call<User> sendUser(@Body User user);
}
