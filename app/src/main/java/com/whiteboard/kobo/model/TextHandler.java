package com.whiteboard.kobo.model;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.FrameLayout;
public class TextHandler extends FrameLayout {
    private EditText editText;
    private Paint borderPaint;

    private float lastTouchX, lastTouchY;
    private boolean isDragging = false;

    public TextHandler(Context context) {
        super(context);
        init();
    }
    public TextHandler(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setWillNotDraw(false); // Allow onDraw to be called

        editText = new EditText(getContext());
        editText.setGravity(Gravity.TOP | Gravity.START);
        editText.setBackground(null);
        editText.setPadding(0, 0, 0, 0);
        editText.setText("Insert Text here");
        editText.setTextColor(Color.BLACK);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                invalidate();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(2f);
        borderPaint.setAntiAlias(true);

        addView(editText);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw a border around the text box
        canvas.drawRect(0, 0, getWidth(), getHeight(), borderPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isInsideTextBox(touchX, touchY)) {
                    isDragging = true;
                    lastTouchX = touchX;
                    lastTouchY = touchY;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (isDragging) {
                    float dx = touchX - lastTouchX;
                    float dy = touchY - lastTouchY;

                    // Move the textbox
                    layout((int) (getLeft() + dx), (int) (getTop() + dy), (int) (getRight() + dx), (int) (getBottom() + dy));

                    lastTouchX = touchX;
                    lastTouchY = touchY;
                }
                break;

            case MotionEvent.ACTION_UP:
                isDragging = false;
                break;
        }

        return true;
    }

    private boolean isInsideTextBox(float touchX, float touchY) {
        return touchX >= 0 && touchX <= getWidth() &&
                touchY >= 0 && touchY <= getHeight();
    }

    public void setBorderColor(int color) {
        borderPaint.setColor(color);
        invalidate();
    }

    public EditText getEditText() {
        return editText;
    }

}

