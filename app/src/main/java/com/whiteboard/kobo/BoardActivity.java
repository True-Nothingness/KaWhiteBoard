package com.whiteboard.kobo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.whiteboard.kobo.model.CurrentBoard;
import com.whiteboard.kobo.model.ImageHandler;
import com.whiteboard.kobo.model.SocketManager;
import com.whiteboard.kobo.model.UserData;
import com.whiteboard.kobo.model.UserResponse;
import com.whiteboard.kobo.model.drawingView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import yuku.ambilwarna.AmbilWarnaDialog;

public class BoardActivity extends AppCompatActivity {
    BottomAppBar bottomAppBar;
    drawingView  drawing_view;
    private int selectedColor = 0;
    FloatingActionButton expand;
    RelativeLayout relativeLayout, mainBoard;
    SeekBar brushSizeSeekbar;
    SeekBar brushOpacitySeekbar;
    TextView sizeLabel;
    TextView opacityLabel;
    Button set;
    MaterialToolbar topBar;
    private Socket socket;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageHandler touchImageView;
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == RESULT_OK) {
                                Intent data = result.getData();
                                if (data != null) {
                                    Uri selectedImageUri = data.getData();
                                    touchImageView.setImageUri(selectedImageUri);
                                }
                            }
                        }
                    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        try {
            socket = IO.socket("http://192.168.0.106:5000/");
            socket.connect();
            SocketManager.setSocket(socket);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Intent homeIntent = new Intent(this, HomeActivity.class);
        topBar = findViewById(R.id.topAppBar);
        Menu menu = topBar.getMenu();
        MenuItem undoItem = menu.findItem(R.id.undo);
        MenuItem redoItem = menu.findItem(R.id.redo);
        MenuItem optionsItem = menu.findItem(R.id.options);
        drawing_view = findViewById(R.id.drawing_view);
        drawing_view.setSocket(socket);
        expand = findViewById(R.id.expandButton);
        relativeLayout = findViewById(R.id.seekbars);
        mainBoard = findViewById(R.id.mainBoard);
        brushSizeSeekbar = findViewById(R.id.brushSizeSeekBar);
        brushOpacitySeekbar = findViewById(R.id.brushOpacitySeekBar);
        sizeLabel = findViewById(R.id.brushSizeLabel);
        opacityLabel = findViewById(R.id.brushOpacityLabel);
        touchImageView = findViewById(R.id.touchImageView);
        set = findViewById(R.id.set);
        // Find the current user in the list
        UserResponse currentUser = null;
        for (UserResponse user : CurrentBoard.getInstance().getUsers()) {
            if (UserData.getInstance().getId().equals(user.getId())) {
                currentUser = user;
                break;
            }
        }
        // Check the role of the current user
        if (currentUser != null) {
            String currentUserRole = currentUser.getRole();
            drawing_view.setUserRole(currentUserRole);
            if ("Viewer".equals(currentUserRole)) {
                expand.hide();
                undoItem.setVisible(false);
                redoItem.setVisible(false);
                optionsItem.setVisible(false);
            } else if ("Editor".equals(currentUserRole)) {
                optionsItem.setVisible(false);
            }
        }
        socket.emit("joinWhiteboard", CurrentBoard.getInstance().getId());
        Log.d("boardId",":"+CurrentBoard.getInstance().getId());
        socket.on("boardData", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    if (args.length > 0 && args[0] instanceof JSONArray) {
                        JSONArray drawingsArray = (JSONArray) args[0];

                        for (int i = 0; i < drawingsArray.length(); i++) {
                            JSONObject drawData = drawingsArray.getJSONObject(i);

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
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
                }
                if(item.getItemId()==R.id.chat){
                    showChatFragment();
                }
                return false;
            }
        });

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (socket != null && socket.connected()) {
            socket.disconnect();
        }
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
    private void showChatFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Replace with your fragment class
        ChatFragment chatFragment = new ChatFragment();

        // Set custom enter animation
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right);

        transaction.replace(R.id.fragmentContainer, chatFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }
    public void addNewTextBox() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final EditText newTextBox = (EditText) inflater.inflate(R.layout.text_box, null);

        // Set a unique identifier for each EditText
        newTextBox.setId(View.generateViewId());

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, R.id.itemButton1);

        newTextBox.setLayoutParams(params);
        mainBoard.addView(newTextBox);

        // Make the new EditText movable
        setMovable(newTextBox);
    }
    private void setMovable(final EditText editText) {
        editText.setOnTouchListener(new View.OnTouchListener() {
            private float x, y;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        x = view.getX() - event.getRawX();
                        y = view.getY() - event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        view.animate()
                                .x(event.getRawX() + x)
                                .y(event.getRawY() + y)
                                .setDuration(0)
                                .start();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
    }
}
