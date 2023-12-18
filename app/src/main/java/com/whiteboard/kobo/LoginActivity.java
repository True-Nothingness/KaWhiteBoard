package com.whiteboard.kobo;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
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
    Button loginbtn;
    Button signupbtn;
    EditText email2;
    EditText password2;
    String emailInput;
    String pwdInput;
    boolean success = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Intent loginIntent = new Intent(this, HomeActivity.class);
        Intent loginIntent2 = new Intent(this, SignupActivity.class);
        loginbtn = findViewById(R.id.loginbtn);
        signupbtn = findViewById(R.id.signupbtn);
        email2 = findViewById(R.id.email2);
        password2 = findViewById(R.id.password2);

        loginbtn.setOnClickListener(
                v -> {
                    emailInput = email2.getText().toString();
                    pwdInput = password2.getText().toString();
                    logIn(emailInput, pwdInput);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // The code inside this run() method will be executed after a 3-second delay
                            if (success) {
                                startActivity(loginIntent);
                            }
                            // Handle other UI updates or show error messages if needed
                        }
                    }, 1500);
                }
        );
        signupbtn.setOnClickListener(
                v -> startActivity(loginIntent2)
        );
    }
    private void logIn(String email, String password){
        Login login = new Login();
        login.setEmail(email);
        login.setPassword(password);
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        apiService.apiService.logIn(login).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
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
                    editor.apply();

                    // Your success handling logic
                    success = true;
                    Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle unsuccessful response
                    success = false;
                    Toast.makeText(LoginActivity.this, "Login Unsuccessfully, please check your info", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Handle network errors or request failure
                success = false;
                Toast.makeText(LoginActivity.this, "Login Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}