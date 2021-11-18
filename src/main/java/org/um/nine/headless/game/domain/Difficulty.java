package org.um.nine.headless.game.domain;

public enum Difficulty {
    EASY(0, 4),
    NORMAL(1, 5),
    HIGH(2, 6);

    private final int id;
    private final int count;

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
