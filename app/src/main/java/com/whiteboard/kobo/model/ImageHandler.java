package com.whiteboard.kobo.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ImageHandler extends androidx.appcompat.widget.AppCompatImageView {
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;

    private int mode = NONE;

    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;
    private float totalTranslationX = 0f;
    private float totalTranslationY = 0f;


    public ImageHandler(Context context) {
        super(context);
        init();
    }

    public ImageHandler(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setScaleType(ScaleType.MATRIX);
    }
    public void setImageUri(Uri uri) {
        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private Bitmap getBitmapFromUri(Uri uri) {
        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    float dx = event.getX() - start.x;
                    float dy = event.getY() - start.y;
                    matrix.postTranslate(dx, dy);

                    // Update total translation
                    totalTranslationX += dx;
                    totalTranslationY += dy;
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }

        setImageMatrix(matrix);

        // Move the entire custom view on the canvas
        setTranslationX(totalTranslationX);
        setTranslationY(totalTranslationY);

        invalidate();
        return true;
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
    class SendDataTask implements Callable<Void> {
        private String serverAddress;
        private int serverPort;
        private ImageData imageData;

        public SendDataTask(String serverAddress, int serverPort, ImageData imageData) {
            this.serverAddress = serverAddress;
            this.serverPort = serverPort;
            this.imageData = imageData;
        }

        @Override
        public Void call() throws Exception {
            try (Socket socket = new Socket(serverAddress, serverPort);
                 ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {

                byte[] imageBytes = convertBitmapToByteArray(imageData.getImage());
                SerializedImageData serializedImageData = new SerializedImageData(imageBytes, imageData.getCoordinates());

                objectOutputStream.writeObject(serializedImageData);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private byte[] convertBitmapToByteArray(Bitmap bitmap) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }
    }
    class SerializedImageData implements Serializable {
        private byte[] imageBytes;
        private Coordinates coordinates;

        public SerializedImageData(byte[] imageBytes, Coordinates coordinates) {
            this.imageBytes = imageBytes;
            this.coordinates = coordinates;
        }

        public byte[] getImageBytes() {
            return imageBytes;
        }

        public Coordinates getCoordinates() {
            return coordinates;
        }
    }

    // Class representing the data to be sent over the network
    class ImageData implements Serializable {
        private Bitmap image;
        private Coordinates coordinates;

        public ImageData(Bitmap image, Coordinates coordinates) {
            this.image = image;
            this.coordinates = coordinates;
        }

        public Bitmap getImage() {
            return image;
        }

        public Coordinates getCoordinates() {
            return coordinates;
        }
    }

    // Class representing the coordinates
    class Coordinates implements Serializable {
        private float x;
        private float y;

        public Coordinates(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}