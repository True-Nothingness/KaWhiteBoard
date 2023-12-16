package com.whiteboard.kobo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
boolean success = false;
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
                    registerUser(nameInput,emailInput,pwdInput);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // The code inside this run() method will be executed after a 3-second delay
                            if (success) {
                                startActivity(signupIntent);
                            }
                            // Handle other UI updates or show error messages if needed
                        }
                    }, 2000);
                }
        );
    }
    private void logIn(String email, String password){
        Login login = new Login();
        login.setEmail(email);
        login.setPassword(password);
        apiService.apiService.logIn(login).enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.isSuccessful()) {
                    success = true;
                    Toast.makeText(SignupActivity.this,"Signup Successfully", Toast.LENGTH_SHORT).show();
                }else {
                    success = false;
                    Toast.makeText(SignupActivity.this,"Signup Unsuccessfully, please check your info", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                success = false;
                Toast.makeText(SignupActivity.this,"Login Error", Toast.LENGTH_SHORT).show();
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