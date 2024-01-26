package com.whiteboard.kobo.model;
import android.graphics.Bitmap;
import java.io.Serializable;

public class ImageData implements Serializable {
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

    // Other setters and getters...
}