package org.um.nine.domain;

public class Resolution {
    private int id;
    private int width;
    private int height;

    public Resolution(int id, int width, int height) {
        this.id = id;
        this.width = width;
        this.height = height;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(width);
        sb.append('x');
        sb.append(height);
        return  sb.toString();
    }
}
