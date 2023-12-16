package com.whiteboard.kobo.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.whiteboard.kobo.model.Login;
import com.whiteboard.kobo.model.LoginResponse;
import com.whiteboard.kobo.model.Register;
import com.whiteboard.kobo.model.User;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface apiService {
    Gson gson = new GsonBuilder()
            .setDateFormat("dd-MM-yyyy HH:mm:ss")
            .create();
    HttpLoggingInterceptor logging = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    OkHttpClient logs = new OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(logging)
            .build();

    apiService apiService = new Retrofit.Builder()
            .baseUrl("http://192.168.1.224:3000/api/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(logs)
            .build()
            .create(apiService.class);
    @POST("auth/register")
    @Headers("Content-Type: application/json")
    Call<Register> createUser(@Body Register register);
    @POST("auth/login")
    @Headers("Content-Type: application/json")
    Call<LoginResponse> logIn(@Body Login login);

}
