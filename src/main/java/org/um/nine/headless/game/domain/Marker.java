package org.um.nine.headless.game.domain;

public class Marker {
    private int id;
    private boolean isCurrent;

    public Marker(int id) {
        this.id = id;
    }

    public Marker(int id, boolean isCurrent) {
        this.id = id;
        this.isCurrent = isCurrent;
    }

    public int getId() {
        return id;
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public void setCurrent(boolean current) {
        isCurrent = current;
    }
}
