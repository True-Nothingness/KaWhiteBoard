package com.whiteboard.kobo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.whiteboard.kobo.api.apiService;
import com.whiteboard.kobo.model.Forgor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgorActivity extends AppCompatActivity {
    Button resetbtn;
    EditText email2;
    String emailInput;
    boolean success = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgor);
        resetbtn = findViewById(R.id.resetbtn);
        email2 = findViewById(R.id.email2);
        Intent forgorIntent = new Intent(this, ResetActivity.class);
        resetbtn.setOnClickListener(
                v -> {
                    emailInput = email2.getText().toString();
                    emailInput = emailInput.trim();
                    forgotPassword(emailInput);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (success) {
                                startActivity(forgorIntent);
                            }
                        }
                    }, 5000);
                }
        );
    }
    private void forgotPassword(String email){
        Forgor forgor = new Forgor();
        forgor.setEmail(email);
        apiService.apiService.forgotPasword(forgor).enqueue(new Callback<Forgor>() {
            @Override
            public void onResponse(Call<Forgor> call, Response<Forgor> response) {
                if (response.isSuccessful()) {
                    success = true;
                    Toast.makeText(ForgorActivity.this, "Reset link sent to your email!", Toast.LENGTH_SHORT).show();
                } else {
                    success = false;
                    Toast.makeText(ForgorActivity.this, "Reset failed, please check your email input!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Forgor> call, Throwable t) {
                success = false;
                Toast.makeText(ForgorActivity.this, "Reset Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}