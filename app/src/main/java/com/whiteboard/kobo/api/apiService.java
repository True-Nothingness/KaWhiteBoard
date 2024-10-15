package com.whiteboard.kobo.api;

import androidx.annotation.NonNull;

import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.whiteboard.kobo.model.Board;
import com.whiteboard.kobo.model.BoardJSON;
import com.whiteboard.kobo.model.Deletion;
import com.whiteboard.kobo.model.Forgor;
import com.whiteboard.kobo.model.Login;
import com.whiteboard.kobo.model.LoginResponse;
import com.whiteboard.kobo.model.PwdReset;
import com.whiteboard.kobo.model.Register;
import com.whiteboard.kobo.model.User;
import com.whiteboard.kobo.model.UserData;
import com.whiteboard.kobo.model.UserResponse;

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
        @NonNull
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
            .baseUrl("http://192.168.0.101:3000/api/")
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
    @POST("password-reset/")
    @Headers("Content-Type: application/json")
    Call<Forgor> forgotPasword(@Body Forgor forgor);
    @POST("password-reset/link")
    @Headers("Content-Type: application/json")
    Call<PwdReset> resetPassword(@Body PwdReset pwdreset);
    @POST("board/create-whiteboard")
    @Headers("Content-Type: application/json")
    Call<BoardJSON> createBoard(@Body BoardJSON boardjson);
    @GET("boards/created-by")
    Call<List<Board>> getOwnedBoard(@Query("userId") String id);
    @GET("boards/member-of")
    Call<List<Board>> getJoinedBoard(@Query("userId") String id);
    @POST("board/delete-whiteboard")
    @Headers("Content-Type: application/json")
    Call<Void> deleteBoard(@Body Deletion deletion);
    @POST("board/join-whiteboard")
    @Headers("Content-Type: application/json")
    Call<Void> joinBoard(@Body com.whiteboard.kobo.model.Request request);
    @POST("board/remove-member")
    @Headers("Content-Type: application/json")
    Call<List<UserResponse>> removeMember(@Query("userId") String id, @Query("boardId") String boardId);
    @POST("board/change-role")
    @Headers("Content-Type: application/json")
    Call<List<UserResponse>> changeRole(@Query("userId") String id, @Query("role") String role, @Query("boardId") String boardId);
}
