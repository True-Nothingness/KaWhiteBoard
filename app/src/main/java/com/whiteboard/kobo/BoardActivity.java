package com.whiteboard.kobo;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import yuku.ambilwarna.AmbilWarnaDialog;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mihir.drawingcanvas.drawingView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import java.util.ArrayList;
import java.util.List;

import com.google.android.material.bottomappbar.BottomAppBar;

import org.jetbrains.annotations.NotNull;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CanvasView canvasView;
        canvasView = new CanvasView((Context) this);
        setContentView(R.layout.activity_board);
        Intent homeIntent = new Intent(this, HomeActivity.class);
        topBar = findViewById(R.id.topAppBar);
        drawing_view = findViewById(R.id.drawing_view);
        expand = findViewById(R.id.expandButton);
        relativeLayout = findViewById(R.id.seekbars);
        brushSizeSeekbar = findViewById(R.id.brushSizeSeekBar);
        brushOpacitySeekbar = findViewById(R.id.brushOpacitySeekBar);
        sizeLabel = findViewById(R.id.brushSizeLabel);
        opacityLabel = findViewById(R.id.brushOpacityLabel);
        set = findViewById(R.id.set);
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
                return false;
            }
        });

    }
    public class CanvasView extends View {

        private final Panning panning;
        private final GridManager gridManager;
        private Rect bounds;
        private Point current = new Point(0, 0);
        private List<Overlay> overlays;
        public CanvasView(Context context) {
            super(context);
            bounds = new Rect();
            panning = new Panning();
            overlays = new ArrayList<>();
            gridManager = new GridManager(this);
            init();
        }

        public void with(String[][] labels, int columns, int rows) {
            gridManager.with(labels, columns, rows);
        }

        private void init() {
            ViewTreeObserver observer = getViewTreeObserver();
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    int width = getWidth();
                    int height = getHeight();
                    bounds.set(0, 0, width, height);
                    gridManager.generate(bounds);
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }

        @Override
        protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
            super.onSizeChanged(width, height, oldWidth, oldHeight);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            new Canvas(bitmap);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            bounds.offsetTo(-current.x, -current.y);
            gridManager.generate(bounds);
            canvas.translate(current.x, current.y);
            for (Overlay overlay : overlays) {
                if (overlay.intersects(bounds)) {
                    overlay.onDraw(canvas);
                }
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            current = panning.handle(event);
            invalidate();
            return true;
        }

        public void addChild(Overlay overlay) {
            this.overlays.add(overlay);
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
}