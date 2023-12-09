package com.whiteboard.kobo.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.whiteboard.kobo.model.Login;
import com.whiteboard.kobo.model.Register;
import com.whiteboard.kobo.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
public interface apiService {
    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();
    apiService apiService = new Retrofit.Builder()
            .baseUrl("http://192.168.1.224:3000/api/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(apiService.class);
    @GET("users")
    Call<List<User>> FetchUsers();
    @POST("auth/register")
    @Headers("Content-Type: application/json")
    Call<Register> createUser(@Body Register register);
    @POST("auth/login")
    @Headers("Content-Type: application/json")
    Call<Login> logIn(@Body Login login);
}
