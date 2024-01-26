package com.whiteboard.kobo.model;

import java.io.Serializable;

public class Coordinates implements Serializable {
    private float x;
    private float y;

    public Coordinates(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
