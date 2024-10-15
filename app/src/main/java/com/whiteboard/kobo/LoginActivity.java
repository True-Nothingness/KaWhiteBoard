package com.whiteboard.kobo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.whiteboard.kobo.api.apiService;
import com.whiteboard.kobo.model.Login;
import com.whiteboard.kobo.model.LoginResponse;
import com.whiteboard.kobo.model.User;
import com.whiteboard.kobo.model.UserData;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    Button loginbtn, signupbtn;
    EditText email2, password2;
    TextView textView2;
    String emailInput, pwdInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Intent loginIntent2 = new Intent(this, SignupActivity.class);
        Intent loginIntent3 = new Intent(this, ForgorActivity.class);
        loginbtn = findViewById(R.id.loginbtn);
        signupbtn = findViewById(R.id.signupbtn);
        email2 = findViewById(R.id.email2);
        password2 = findViewById(R.id.password2);
        textView2 = findViewById(R.id.textView2);

        loginbtn.setOnClickListener(
                v -> {
                    emailInput = email2.getText().toString();
                    pwdInput = password2.getText().toString();
                    emailInput = emailInput.trim();
                    logIn(emailInput, pwdInput);
                }
        );
        signupbtn.setOnClickListener(
                v -> startActivity(loginIntent2)
        );
        textView2.setOnClickListener(
                v -> startActivity(loginIntent3)
        );
    }
    private void logIn(String email, String password){
        Login login = new Login();
        login.setEmail(email);
        login.setPassword(password);
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        apiService.apiService.logIn(login).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    if(loginResponse==null) return;
                    String authToken = loginResponse.getToken();
                    User user = loginResponse.getUser();

                    // Handle the extracted information as needed
                    String userId = user.getId();
                    String userName = user.getName();
                    String userEmail = user.getEmail();
                    UserData.getInstance().setUsername(userName);
                    UserData.getInstance().setEmail(userEmail);
                    UserData.getInstance().setId(userId);
                    UserData.getInstance().setToken(authToken);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("authToken", authToken);
                    editor.putString("userEmail", userEmail);
                    editor.putString("userName", userName);
                    editor.putString("userId", userId);
                    editor.apply();
                    // Your success handling logic
                    Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                    Intent loginIntent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(loginIntent);
                    finish();
                } else {
                    // Handle unsuccessful response
                    Toast.makeText(LoginActivity.this, "Login Unsuccessfully, please check your info", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                // Handle network errors or request failure
                Toast.makeText(LoginActivity.this, "Login Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}