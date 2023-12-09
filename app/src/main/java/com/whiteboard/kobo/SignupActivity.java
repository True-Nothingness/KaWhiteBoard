package com.whiteboard.kobo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.whiteboard.kobo.api.apiService;
import com.whiteboard.kobo.model.Login;
import com.whiteboard.kobo.model.Register;

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
        Intent signupIntent = new Intent(this, HomeActivity.class);
        signupbtn = findViewById(R.id.signupbtn);
        email2 = findViewById(R.id.email2);
        name2 = findViewById(R.id.name2);
        password2 = findViewById(R.id.password2);

        signupbtn.setOnClickListener(
                v -> {
                    emailInput = email2.getText().toString();
                    nameInput = name2.getText().toString();
                    pwdInput = password2.getText().toString();
                    registerUser();
                    startActivity(signupIntent);
                }
        );
    }
    private void logIn(){
        Login login = new Login();
        login.setUserEmail(emailInput);
        login.setUserPwd(pwdInput);

        apiService.apiService.logIn(login).enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SignupActivity.this,"Login Successfully", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(SignupActivity.this,"Login Unsuccessfully, please check your info", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                Toast.makeText(SignupActivity.this,"Login Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void registerUser(){
        Register register = new Register();
        register.setUserEmail(emailInput);
        register.setUserName(nameInput);
        register.setUserPwd(pwdInput);

        apiService.apiService.createUser(register).enqueue(new Callback<Register>() {
            @Override
            public void onResponse(Call<Register> call, Response<Register> response) {
                if (response.isSuccessful()) {
                Toast.makeText(SignupActivity.this,"Register Successfully", Toast.LENGTH_SHORT).show();
                    logIn();
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