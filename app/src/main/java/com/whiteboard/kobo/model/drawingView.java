package com.whiteboard.kobo.model;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import io.socket.client.IO;
import io.socket.client.Socket;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.util.ArrayList;

public class drawingView extends View {

    private CustomPath mDrawPath;
    private Bitmap mCanvasBitmap;
    private Paint mDrawPaint;
    private Paint mCanvasPaint;
    private int mBrushSize;
    private int currentColor = Color.BLACK;
    private Canvas canvas;
    private int mAlpha = 255;
    private ArrayList<CustomPath> mPaths = new ArrayList<>();
    private ArrayList<CustomPath> mUndoPath = new ArrayList<>();
    private float mScaleFactor = 1.0f;
    private float mLastTouchX;
    private float mLastTouchY;
    private float mPosX = 0.0f;
    private float mPosY = 0.0f;
    private boolean isDragging, isZooming = false;
    private float originalContentWidth;
    private float originalContentHeight;
    private float previousTranslateX = 0f;
    private float previousTranslateY = 0f;
    private ScaleGestureDetector mScaleDetector;
    private Socket socket;
    private String userRole;
    private final Object lock = new Object();


    public drawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUpDrawing();
    }

    private void setUpDrawing() {
        mDrawPaint = new Paint();
        mDrawPath = new CustomPath(currentColor, mBrushSize, mAlpha);
        mDrawPaint.setColor(currentColor);
        mDrawPaint.setStyle(Paint.Style.STROKE);
        mDrawPaint.setAlpha(mAlpha);
        mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
        mDrawPaint.setStrokeCap(Paint.Cap.ROUND);
        mCanvasPaint = new Paint(Paint.DITHER_FLAG);
        mBrushSize = 20;
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        originalContentWidth = w;
        originalContentHeight = h;
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(mCanvasBitmap);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        // Save the current state of the canvas before applying transformations
        canvas.save();

        // Apply translation (for panning) and scaling (for zooming)
        canvas.translate(mPosX, mPosY);
        canvas.scale(mScaleFactor, mScaleFactor);

        // Draw the bitmap (the base layer of the drawing)
        canvas.drawBitmap(mCanvasBitmap, 0f, 0f, mCanvasPaint);

        // Draw all saved paths
        synchronized (lock){
            if (!mPaths.isEmpty()){
                for (CustomPath path : mPaths) {
                mDrawPaint.setStrokeWidth(path.brushThickness);
                mDrawPaint.setColor(path.color);
                mDrawPaint.setAlpha(path.alpha);
                canvas.drawPath(path, mDrawPaint);
                }
            }
        }
        // Draw the current path being drawn, if it's not empty
        if (!mDrawPath.isEmpty()) {
            mDrawPaint.setStrokeWidth(mDrawPath.brushThickness);
            mDrawPaint.setColor(mDrawPath.color);
            mDrawPaint.setAlpha(mDrawPath.alpha);
            canvas.drawPath(mDrawPath, mDrawPaint);
        }

        // Restore the canvas to its original state
        canvas.restore();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleDetector.onTouchEvent(event);
        float touchX = (event.getX() - mPosX) / mScaleFactor;
        float touchY = (event.getY() - mPosY) / mScaleFactor;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(!isDragging && !isZooming){
                    if ("Viewer".equals(userRole)) {
                        // User has Viewer role, don't process touch events
                        break;
                    }
                mLastTouchX = touchX;
                mLastTouchY = touchY;
                mDrawPath.color = currentColor;
                mDrawPath.brushThickness = mBrushSize;
                mDrawPath.alpha = mAlpha;
                mDrawPath.reset();
                mDrawPath.moveTo(touchX, touchY);
                mDrawPath.setMoveTo(touchX, touchY);}
                break;
            case MotionEvent.ACTION_MOVE:
                if (isDragging) {
                    float dx = event.getX() - mLastTouchX;
                    float dy = event.getY() - mLastTouchY;

                    mPosX += dx / mScaleFactor;
                    mPosY += dy / mScaleFactor;

                    mLastTouchX = event.getX();
                    mLastTouchY = event.getY();
                } else if (!isZooming) {
                    if ("Viewer".equals(userRole)) {
                        // User has Viewer role, don't process touch events
                        break;
                    }
                    mDrawPath.lineTo(touchX, touchY);
                    mDrawPath.addPoint(touchX, touchY);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!isZooming) {  // Avoid drawing while zooming
                    if ("Viewer".equals(userRole)) {
                        // User has Viewer role, don't process touch events
                        break;
                    }
                    // Finish the current path and add it to the list of paths
                    mPaths.add(mDrawPath);
                    emitDrawEvent(mDrawPath);
                    mDrawPath = new CustomPath(currentColor, mBrushSize, mAlpha);
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                isDragging = true;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                isDragging = false;
                break;
            default:
                return false;
        }
        mLastTouchX = touchX;
        mLastTouchY = touchY;
        invalidate();
        return true;
    }
    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    public void setSizeForBrush(int newSize) {
        mBrushSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        mDrawPaint.setStrokeWidth(mBrushSize);
    }

    public int getBrushSize() {
        return mBrushSize;
    }

    public void setBrushAlpha(int newAlpha) {
        mAlpha = newAlpha;
        mDrawPaint.setAlpha(newAlpha);
    }

    public int getBrushAlpha() {
        return mAlpha;
    }

    public void setBrushColor(int color) {
        currentColor = color;
        mDrawPaint.setColor(color);
    }

    public int getBrushColor() {
        return currentColor;
    }

    public void erase(int colorBackground) {
        mAlpha = 255;
        mDrawPaint.setAlpha(255);
        currentColor = colorBackground;
        mDrawPaint.setColor(colorBackground);
    }

    public void undo() {
        if (!mPaths.isEmpty()) {
            mUndoPath.add(mPaths.get(mPaths.size() - 1));
            mPaths.remove(mPaths.size() - 1);
            emitUndoEvent();
            invalidate();
        }
    }
    public void undo2() {
        if (!mPaths.isEmpty()) {
            mUndoPath.add(mPaths.get(mPaths.size() - 1));
            mPaths.remove(mPaths.size() - 1);
            invalidate();
        }
    }

    public void redo() {
        if (!mUndoPath.isEmpty()) {
            mPaths.add(mUndoPath.get(mUndoPath.size() - 1));
            mUndoPath.remove(mUndoPath.size() - 1);
            emitRedoEvent();
            invalidate();
        }
    }
    public void redo2() {
        if (!mUndoPath.isEmpty()) {
            mPaths.add(mUndoPath.get(mUndoPath.size() - 1));
            mUndoPath.remove(mUndoPath.size() - 1);
            invalidate();
        }
    }

    public void clearDrawingBoard() {
        mPaths.clear();
        invalidate();
    }

    public ArrayList<CustomPath> getDrawing() {
        return mPaths;
    }

    public static class CustomPath extends Path {
        public int color;
        public int brushThickness;
        public int alpha;
        private PointF moveTo;  // Starting point
        private ArrayList<PointF> points;  // List of points for lineTo

        public CustomPath(int color, int brushThickness, int alpha) {
            this.color = color;
            this.brushThickness = brushThickness;
            this.alpha = alpha;
            this.moveTo = new PointF();
            this.points = new ArrayList<>();
        }
        public void setMoveTo(float x, float y) {
            moveTo.set(x, y);
        }

        public void addPoint(float x, float y) {
            points.add(new PointF(x, y));
        }

        public PointF getMoveTo() {
            return moveTo;
        }

        public ArrayList<PointF> getPoints() {
            return points;
        }
    }
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 3.0f)); // Limit the scale factor
            invalidate();
            return true;
        }
        @Override
        public boolean onScaleBegin(@NonNull ScaleGestureDetector detector) {
            isZooming = true;  // Zooming starts
            return true;
        }

        @Override
        public void onScaleEnd(@NonNull ScaleGestureDetector detector) {
            isZooming = false;  // Zooming ends
        }

    }
    public void emitDrawEvent(CustomPath customPath) {
        try {
            JSONObject drawData = new JSONObject();
            drawData.put("color", customPath.color);
            drawData.put("brushThickness", customPath.brushThickness);
            drawData.put("alpha", customPath.alpha);
            // Add starting point (moveTo) to the JSON data
            JSONObject moveTo = new JSONObject();
            moveTo.put("x", customPath.getMoveTo().x);
            moveTo.put("y", customPath.getMoveTo().y);
            drawData.put("moveTo", moveTo);

            // Add list of points (lineTo) to the JSON data
            JSONArray pointsArray = new JSONArray();
            for (PointF point : customPath.getPoints()) {
                JSONObject pointObject = new JSONObject();
                pointObject.put("x", point.x);
                pointObject.put("y", point.y);
                pointsArray.put(pointObject);
            }
            drawData.put("points", pointsArray);
            // Emit the "draw" event to the server
            socket.emit("draw", drawData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void emitUndoEvent() {
        socket.emit("undo");
    }
    public void emitRedoEvent() {
        socket.emit("redo");
    }
    public void updateDrawingView(CustomPath path, float moveX, float moveY, JSONArray pointsArray) throws JSONException {
        path.moveTo(moveX, moveY);
        for (int j = 0; j < pointsArray.length(); j++) {
            JSONObject pointObject = pointsArray.getJSONObject(j);
            float x = (float) pointObject.getDouble("x");
            float y = (float) pointObject.getDouble("y");
            path.lineTo(x, y);
        }
        synchronized (lock){
            mPaths.add(path);
        }
        invalidate();
    }
    public void saveImageToGallery(String fileName) {
        Bitmap bitmap = mCanvasBitmap; // The bitmap to save
        Log.d("Bitmap Info", "Width: " + bitmap.getWidth() + ", Height: " + bitmap.getHeight());
        // Check if bitmap is initialized and has content
        if (bitmap.getWidth() == 0 || bitmap.getHeight() == 0) {
            Toast.makeText(getContext(), "No content to save", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add the .png extension if not present
        if (!fileName.toLowerCase().endsWith(".png")) {
            fileName += ".png";
        }

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");

        // For Android Q (API 29) and above, specify the relative path to the Pictures directory
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
        }

        // Insert the image in MediaStore
        Uri uri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        // Check if the insertion was successful
        if (uri == null) {
            Toast.makeText(getContext(), "Failed to insert image into MediaStore", Toast.LENGTH_SHORT).show();
            return;
        }

        try (OutputStream outputStream = getContext().getContentResolver().openOutputStream(uri)) {
            // Compress the bitmap and save it to the stream
            if (outputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                Toast.makeText(getContext(), "Image saved as " + fileName, Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }


    public void drawBitmap(Bitmap bitmap, int i, int i1, Object o){

    }
    public void setUserRole(String role) {
        this.userRole = role;
    }
}


