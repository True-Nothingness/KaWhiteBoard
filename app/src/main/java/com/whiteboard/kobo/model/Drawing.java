package com.whiteboard.kobo.model;

import java.util.List;

public class Drawing {
    private String board;
    private int color;
    private int brushThickness;
    private int alpha;
    private Point moveTo;
    private List<Point> points;

    public Drawing() {
    }

    public Drawing(String board, int color, int brushThickness, int alpha, Point moveTo, List<Point> points) {
        this.board = board;
        this.color = color;
        this.brushThickness = brushThickness;
        this.alpha = alpha;
        this.moveTo = moveTo;
        this.points = points;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getBrushThickness() {
        return brushThickness;
    }

    public void setBrushThickness(int brushThickness) {
        this.brushThickness = brushThickness;
    }

    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public Point getMoveTo() {
        return moveTo;
    }

    public void setMoveTo(Point moveTo) {
        this.moveTo = moveTo;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public static class Point {
        private int x;
        private int y;

        public Point() {
        }

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }
}
