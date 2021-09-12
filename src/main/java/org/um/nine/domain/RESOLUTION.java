package org.um.nine.domain;

import java.util.Arrays;

public enum RESOLUTION {
    RES_4K(0, "4K", 3840, 2160),
    RES_3K(1, "3K", 2880, 1620),
    RES_2K(2, "2K", 2048, 1080),
    RES_1080(3, "1080p", 1920, 1080),
    RES_1360(4, "768p", 1360, 768),
    RES_720(5, "720p", 1280, 720),
    RES_480(6, "480p", 640, 480);

    private int id;
    private String name;
    private int width;
    private int height;

    RESOLUTION(int id, String name, int width, int height) {
        this.id = id;
        this.name = name;
        this.width = width;
        this.height = height;
    }

    public static RESOLUTION findByResolution(int width, int height) {
        return Arrays.stream(RESOLUTION.values()).filter(item -> {
            return item.getHeight() == height && item.getWidth() == width;
        }).findFirst().orElse(null);
    }

    public String getName() {
        return name;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
