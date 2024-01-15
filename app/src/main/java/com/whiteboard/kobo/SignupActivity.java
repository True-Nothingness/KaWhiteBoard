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
import com.whiteboard.kobo.model.Register;
import com.whiteboard.kobo.model.User;
import com.whiteboard.kobo.model.UserData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {
Button      signupbtn;
EditText    email2;
EditText    name2;
EditText    password2;
String pwdInput;
String nameInput;
String emailInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        signupbtn = findViewById(R.id.signupbtn);
        email2 = findViewById(R.id.email2);
        name2 = findViewById(R.id.name2);
        password2 = findViewById(R.id.password2);

        signupbtn.setOnClickListener(
                v -> {
                    emailInput = email2.getText().toString();
                    nameInput = name2.getText().toString();
                    pwdInput = password2.getText().toString();
                    registerUser(nameInput,emailInput,pwdInput);
                }
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
                    editor.putString("userName", userName);
                    editor.putString("userId", userId);
                    editor.apply();
                    // Your success handling logic
                    Toast.makeText(SignupActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                    Intent signupIntent = new Intent(SignupActivity.this, HomeActivity.class);
                    startActivity(signupIntent);
                    finish();
                } else {
                    // Handle unsuccessful response
                    Toast.makeText(SignupActivity.this, "Login Unsuccessfully, please check your info", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Handle network errors or request failure
                Toast.makeText(SignupActivity.this, "Login Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void registerUser(String userName, String userEmail, String userPwd){
        Register register = new Register();
        register.setUserEmail(userEmail);
        register.setUserName(userName);
        register.setUserPwd(userPwd);

        apiService.apiService.createUser(register).enqueue(new Callback<Register>() {
            @Override
            public void onResponse(Call<Register> call, Response<Register> response) {
                if (response.isSuccessful()) {
                Toast.makeText(SignupActivity.this,"Register Successfully", Toast.LENGTH_SHORT).show();
                    logIn(emailInput, pwdInput);
            }else {
                    Toast.makeText(SignupActivity.this,"Register Unsuccessfully, please check your info", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Register> call, Throwable t) {
                Toast.makeText(SignupActivity.this,"Register Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}