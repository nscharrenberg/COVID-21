package org.um.nine.headless.game.domain;

public class Marker {
    private final int id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Marker marker = (Marker) o;
        return id == marker.id &&
                isCurrent == marker.isCurrent;
    }

}
