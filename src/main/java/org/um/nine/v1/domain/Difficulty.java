package org.um.nine.v1.domain;

public enum Difficulty {
    EASY(0, 4),
    NORMAL(1, 5),
    HIGH(2, 6);

    private int id;
    private int count;

    Difficulty(int id, int count) {
        this.id = id;
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public int getCount() {
        return count;
    }
}
