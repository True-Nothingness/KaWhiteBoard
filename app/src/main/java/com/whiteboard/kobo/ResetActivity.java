package com.whiteboard.kobo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.whiteboard.kobo.api.apiService;
import com.whiteboard.kobo.model.PwdReset;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetActivity extends AppCompatActivity {
    Button resetbtn;
    EditText password2;
    EditText key2;
    String pwdInput;
    boolean success = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
        resetbtn = findViewById(R.id.resetbtn);
        password2 = findViewById(R.id.password2);
        key2 = findViewById(R.id.key2);
        Intent resetIntent = new Intent(this,LoginActivity.class);

        resetbtn.setOnClickListener(
                v -> {
                    String link = key2.getText().toString();
                    String[] segments = link.split("/");
                    String userId = segments[0];
                    String token = segments[1];
                    pwdInput = password2.getText().toString();
                    resetPassword(userId, token, pwdInput);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // The code inside this run() method will be executed after a 3-second delay
                            if (success) {
                                startActivity(resetIntent);
                            }
                            // Handle other UI updates or show error messages if needed
                        }
                    }, 2000);
                }
        );

    }
    private void resetPassword(String userId, String token, String password){
        PwdReset pwdreset = new PwdReset();
        pwdreset.setUserId(userId);
        pwdreset.setToken(token);
        pwdreset.setPassword(password);

        apiService.apiService.resetPassword(pwdreset).enqueue(new Callback<PwdReset>() {
            @Override
            public void onResponse(Call<PwdReset> call, Response<PwdReset> response) {
                if (response.isSuccessful()) {
                    success = true;
                    Toast.makeText(ResetActivity.this, "Password changed successfully, please log in again!", Toast.LENGTH_SHORT).show();
                } else {
                    success =false;
                    Toast.makeText(ResetActivity.this, "Password changed failed!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PwdReset> call, Throwable t) {
                success = false;
                Toast.makeText(ResetActivity.this, "Reset Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}