package com.whiteboard.kobo.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.provider.MediaStore;

import java.io.IOException;

public class ImageHandler extends View {

    private Bitmap image;
    private Matrix matrix;
    private float[] matrixValues = new float[9];

    private float lastTouchX, lastTouchY;
    private boolean isDragging = false;

    private ScaleGestureDetector scaleGestureDetector;

    public ImageHandler(Context context) {
        super(context);
        init();
    }

    public ImageHandler(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        matrix = new Matrix();
        scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        setOnTouchListener(new TouchListener());
    }

    public void setImageUri(Uri imageUri) {
        try {
            image = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
            invalidate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class TouchListener implements OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            scaleGestureDetector.onTouchEvent(event);

            float touchX = event.getX();
            float touchY = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (isInsideImage(touchX, touchY)) {
                        isDragging = true;
                        lastTouchX = touchX;
                        lastTouchY = touchY;
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (isDragging) {
                        float dx = touchX - lastTouchX;
                        float dy = touchY - lastTouchY;

                        matrix.postTranslate(dx, dy);
                        invalidate();

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
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            matrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            invalidate();
            return true;
        }
    }

    private boolean isInsideImage(float touchX, float touchY) {
        return touchX >= matrixValues[Matrix.MTRANS_X] && touchX <= matrixValues[Matrix.MTRANS_X] + image.getWidth() &&
                touchY >= matrixValues[Matrix.MTRANS_Y] && touchY <= matrixValues[Matrix.MTRANS_Y] + image.getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (image != null) {
            canvas.drawBitmap(image, matrix, null);
            matrix.getValues(matrixValues);
        }
    }
}