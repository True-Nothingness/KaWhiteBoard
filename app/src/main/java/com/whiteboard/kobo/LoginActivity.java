package com.whiteboard.kobo;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.whiteboard.kobo.api.apiService;
import com.whiteboard.kobo.model.Login;

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
                    logIn();
                    if (success){startActivity(loginIntent);}
                }
        );
        signupbtn.setOnClickListener(
                v -> startActivity(loginIntent2)
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
                    success = true;
                Toast.makeText(LoginActivity.this,"Login Successfully", Toast.LENGTH_SHORT).show();
            }else {
                    success = false;
                    Toast.makeText(LoginActivity.this,"Login Unsuccessfully, please check your info", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                success = false;
                Toast.makeText(LoginActivity.this,"Login Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}