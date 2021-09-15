package org.um.nine.domain;

import java.util.Arrays;

public enum SAMPLING {
    DISABLED(0, "Disabled", 0),
    X2(1, "2x", 2),
    X4(2, "4x", 4),
    X6(3, "6x", 6),
    X8(4, "8x", 8),
    X16(5, "16x", 16);

    private int id;
    private String name;
    private int value;

    SAMPLING(int id, String name, int value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public static SAMPLING findByValue(int value) {
        return Arrays.stream(SAMPLING.values()).filter(item -> {
            return item.getValue() == value;
        }).findFirst().orElse(null);
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
