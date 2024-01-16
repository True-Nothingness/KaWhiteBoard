package com.whiteboard.kobo;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import io.socket.emitter.Emitter;
import yuku.ambilwarna.AmbilWarnaDialog;
import io.socket.client.IO;
import io.socket.client.Socket;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.whiteboard.kobo.model.CurrentBoard;
import com.whiteboard.kobo.model.drawingView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import com.google.android.material.bottomappbar.BottomAppBar;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class BoardActivity extends AppCompatActivity {
    BottomAppBar bottomAppBar;
    drawingView  drawing_view;
    private int selectedColor = 0;
    FloatingActionButton expand;
    RelativeLayout relativeLayout;
    SeekBar brushSizeSeekbar;
    SeekBar brushOpacitySeekbar;
    TextView sizeLabel;
    TextView opacityLabel;
    Button set;
    MaterialToolbar topBar;
    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        try {
            socket = IO.socket("http://192.168.1.224:5000/");
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Intent homeIntent = new Intent(this, HomeActivity.class);
        topBar = findViewById(R.id.topAppBar);
        drawing_view = findViewById(R.id.drawing_view);
        drawing_view.setSocket(socket);
        expand = findViewById(R.id.expandButton);
        relativeLayout = findViewById(R.id.seekbars);
        brushSizeSeekbar = findViewById(R.id.brushSizeSeekBar);
        brushOpacitySeekbar = findViewById(R.id.brushOpacitySeekBar);
        sizeLabel = findViewById(R.id.brushSizeLabel);
        opacityLabel = findViewById(R.id.brushOpacityLabel);
        set = findViewById(R.id.set);
        socket.emit("joinWhiteboard", CurrentBoard.getInstance().getId());
        Log.d("boardId",":"+CurrentBoard.getInstance().getId());
        socket.on("boardData", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

            }
        });
        socket.on("draw", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    if (args.length > 0 && args[0] instanceof JSONObject) {
                        JSONObject drawData = (JSONObject) args[0];

                        // Extract path details from the JSON object
                        int color = drawData.getInt("color");
                        int brushThickness = drawData.getInt("brushThickness");
                        int alpha = drawData.getInt("alpha");

                        // Extract starting point (moveTo) from the JSON object
                        JSONObject moveTo = drawData.getJSONObject("moveTo");
                        float moveX = (float) moveTo.getDouble("x");
                        float moveY = (float) moveTo.getDouble("y");

                        // Create a new path and move to the starting point
                        drawingView.CustomPath path = new drawingView.CustomPath(color, brushThickness, alpha);
                        path.moveTo(moveX, moveY);

                        // Extract list of points (lineTo) from the JSON object
                        JSONArray pointsArray = drawData.getJSONArray("points");

                        drawing_view.updateDrawingView(path, moveX, moveY, pointsArray);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        socket.on("undo", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        drawing_view.undo2();
                    }
                });
            }
        });
        socket.on("redo", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        drawing_view.redo2();
                    }
                });
            }
        });
        expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyBottomSheetDialogFragment bottomSheetFragment = new MyBottomSheetDialogFragment();
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });
        topBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(homeIntent);
                socket.disconnect();
            }
        });
        topBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.redo){
                    drawing_view.redo();
                }
                if(item.getItemId()==R.id.undo){
                    drawing_view.undo();
                }
                if(item.getItemId()==R.id.options){
                    showAdditionalOptionsFragment();
                    expand.hide();
                }
                return false;
            }
        });

    }
    public void showColorPickerDialog() {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, selectedColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                // Handle onCancel
            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, @ColorInt int color) {
                // Handle onOk
                selectedColor = color;
                drawing_view.setBrushColor(selectedColor);
                // Use the selected color as needed
                Toast.makeText(BoardActivity.this, "Selected Color: #" + Integer.toHexString(color), Toast.LENGTH_SHORT).show();
            }
        });
        colorPicker.show();
    }
    public void updateBrushSizeLabel(int size) {
        sizeLabel.setText("Brush Size: " + size);
        // You can also update your brush size or perform other actions here
    }
    public void updateBrushOpacityLabel(int alpha) {
        opacityLabel.setText("Brush Opacity: " + alpha);
        // You can also update your brush size or perform other actions here
    }
    private void toggleSeekBar() {
        int visibility = relativeLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
        relativeLayout.setVisibility(visibility);
        relativeLayout.setVisibility(visibility);
    }
    public void getSeekbars(){
        toggleSeekBar();
        updateBrushSizeLabel(brushSizeSeekbar.getProgress());
        updateBrushOpacityLabel(brushOpacitySeekbar.getProgress());
        brushSizeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Handle progress change, e.g., update brush size label
                updateBrushSizeLabel(progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Called when user starts dragging the SeekBar
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                drawing_view.setSizeForBrush(brushSizeSeekbar.getProgress());
            }
        });
        brushOpacitySeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Handle progress change, e.g., update brush size label
                updateBrushOpacityLabel(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Called when user starts dragging the SeekBar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                drawing_view.setBrushAlpha(brushOpacitySeekbar.getProgress());
            }
        });
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSeekBar();
            }
        });
    }
    private void showAdditionalOptionsFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Replace with your fragment class
        OptionsFragment optionsFragment = new OptionsFragment();

        // Set custom enter animation
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right);

        transaction.replace(R.id.fragmentContainer, optionsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}