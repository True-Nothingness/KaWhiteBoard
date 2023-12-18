package com.whiteboard.kobo.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.whiteboard.kobo.model.Login;
import com.whiteboard.kobo.model.LoginResponse;
import com.whiteboard.kobo.model.Register;
import com.whiteboard.kobo.model.User;
import com.whiteboard.kobo.model.UserData;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface apiService {
    Gson gson = new GsonBuilder()
            .setDateFormat("dd-MM-yyyy HH:mm:ss")
            .create();
    HttpLoggingInterceptor logging = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    class AuthInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            // Retrieve the token from your UserData singleton or wherever it is stored
            String authToken = UserData.getInstance().getToken();
            // Add the token to the request header
            Request originalRequest = chain.request();
            if (originalRequest.url().toString().contains("/users")) {
                Request newRequest = originalRequest.newBuilder()
                        .header("auth-token", authToken)
                        .build();

                // Proceed with the modified request
                return chain.proceed(newRequest);
            }
            return chain.proceed(originalRequest);
        }
    }
    OkHttpClient logs = new OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(logging)
            .addInterceptor(new AuthInterceptor())
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
    @GET("users")
    Call<User> getUserByEmail(@Query("email") String email);


}
