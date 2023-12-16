package com.whiteboard.kobo;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import yuku.ambilwarna.AmbilWarnaDialog;
import com.mihir.drawingcanvas.drawingView;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;

import org.jetbrains.annotations.NotNull;

public class BoardActivity extends AppCompatActivity {
    BottomAppBar bottomAppBar;
    drawingView  drawing_view;
    private int selectedColor = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        bottomAppBar = findViewById(R.id.bottomAppBar);
        drawing_view = findViewById(R.id.drawing_view);
        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.undo)
                    drawing_view.undo();
                else if (item.getItemId() == R.id.redo) {
                    drawing_view.redo();
                } else if (item.getItemId() == R.id.brushColor) {
                    showColorPickerDialog();
                } else if (item.getItemId()==R.id.eraser) {
                    drawing_view.erase(android.R.color.white);
                }
                return false;
            }
        });
    }
        private void showColorPickerDialog() {
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
}