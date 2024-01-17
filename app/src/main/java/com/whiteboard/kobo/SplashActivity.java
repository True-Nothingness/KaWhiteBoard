package com.whiteboard.kobo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;

import com.whiteboard.kobo.model.User;
import com.whiteboard.kobo.api.apiService;
import com.whiteboard.kobo.model.UserData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class SplashActivity extends AppCompatActivity {
    boolean success = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Intent startIntent = new Intent(this, HomeActivity.class);
        Intent startIntent2 = new Intent(this, LoginActivity.class);
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String authToken = preferences.getString("authToken", null);
        String userEmail = preferences.getString("userEmail", null);
        String userName = preferences.getString("userName", null);
        String userId = preferences.getString("userId", null);
        UserData.getInstance().setToken(authToken);
        UserData.getInstance().setEmail(userEmail);
        UserData.getInstance().setUsername(userName);
        UserData.getInstance().setId(userId);
        if(authToken!=null){
            verify();
            }
        new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                // The code inside this run() method will be executed after a 3-second delay
                if (success) {
                    startActivity(startIntent);
                } else {startActivity(startIntent2);}
                // Handle other UI updates or show error messages if needed
            }
        }, 1500);
    }
    private void verify() {
        apiService.apiService.getUserByEmail(UserData.getInstance().getEmail()).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    success = true;
                } else {
                    success = false;
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                success = false;
            }
        });
    }
}