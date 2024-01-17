package com.whiteboard.kobo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.whiteboard.kobo.FilesFragment;
import com.whiteboard.kobo.SharedFragment;
import com.google.android.material.navigation.NavigationView;
import com.whiteboard.kobo.R;
import com.whiteboard.kobo.api.apiService;
import com.whiteboard.kobo.model.Board;
import com.whiteboard.kobo.model.BoardJSON;
import com.whiteboard.kobo.model.UserData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawerLayout;
    private TextView navHeaderTextView;
    FloatingActionButton addBoard, joinBoard;
    ExtendedFloatingActionButton addFab;
    TextView addText, joinText;
    Boolean isAllFabsVisible;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String authToken = preferences.getString("authToken", null);
        String userEmail = preferences.getString("userEmail", null);
        String userName = preferences.getString("userName", null);
        String userId = preferences.getString("userId", null);
        UserData.getInstance().setToken(authToken);
        UserData.getInstance().setEmail(userEmail);
        UserData.getInstance().setUsername(userName);
        UserData.getInstance().setId(userId);
        drawerLayout = findViewById(R.id.drawer_layout);
        addFab = findViewById(R.id.add_fab);
        joinBoard = findViewById(R.id.join_board_fab);
        addBoard = findViewById(R.id.addBoard);
        joinText = findViewById(R.id.join_board_action_text);
        addText = findViewById(R.id.add_board_action_text);

        joinBoard.setVisibility(View.GONE);
        joinText.setVisibility(View.GONE);
        addBoard.setVisibility(View.GONE);
        addText.setVisibility(View.GONE);
        isAllFabsVisible = false;
        addFab.shrink();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav,R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if(savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FilesFragment()).commit();
            navigationView.setCheckedItem(R.id.my_files);
        }
        View headerView = navigationView.getHeaderView(0);
        navHeaderTextView = headerView.findViewById(R.id.nametag);
        navHeaderTextView.setText(preferences.getString("userName", null));
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isAllFabsVisible){
                    joinBoard.show();
                    addBoard.show();
                    joinText.setVisibility(View.VISIBLE);
                    addText.setVisibility(View.VISIBLE);
                    addFab.extend();
                    isAllFabsVisible = true;
                }else{
                    joinBoard.hide();
                    addBoard.hide();
                    joinText.setVisibility(View.GONE);
                    addText.setVisibility(View.GONE);
                    addFab.shrink();
                    isAllFabsVisible = false;
                }
            }
        });
        addBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog1();
            }
        });
        joinBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog2();
            }
        });
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.my_files:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FilesFragment()).commit();
                break;
            case R.id.shared_with:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SharedFragment()).commit();
                break;
            case R.id.logout:
                Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show();
                SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove("authToken");
                editor.remove("userEmail");
                editor.remove("userName");
                editor.remove("userId");
                editor.apply();
                Intent logOut = new Intent(this, LoginActivity.class);
                startActivity(logOut);
                finish();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    private void showAlertDialog1() {
        // Create an EditText widget programmatically
        final EditText editText = new EditText(this);

        // Create AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter new board name:")
                .setView(editText)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle OK button click
                        String enteredText = editText.getText().toString();
                        createBoard(enteredText);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle Cancel button click
                        dialog.cancel();
                    }
                });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void showAlertDialog2() {
        // Create an EditText widget programmatically
        final EditText editText = new EditText(this);

        // Create AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter board code to join:")
                .setView(editText)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle OK button click
                        String enteredText = editText.getText().toString();
                        //add joinboard function
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle Cancel button click
                        dialog.cancel();
                    }
                });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void createBoard(String boardName){
        BoardJSON boardjson = new BoardJSON();
        boardjson.setBoardName(boardName);
        boardjson.setCreatorId(UserData.getInstance().getId());
        boardjson.setCreatorName(UserData.getInstance().getUsername());
        Log.d("Creator's ID",":" + UserData.getInstance().getId());
        apiService.apiService.createBoard(boardjson).enqueue(new Callback<BoardJSON>() {
            @Override
            public void onResponse(Call<BoardJSON> call, Response<BoardJSON> response) {
                if (response.isSuccessful()){
                    Toast.makeText(HomeActivity.this, "Created Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(HomeActivity.this, BoardActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Handle unsuccessful response
                    Toast.makeText(HomeActivity.this, "Board not created!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BoardJSON> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "Creation Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onBackPressed(){
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else{
            super.onBackPressed();
        }
    }
}