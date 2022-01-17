package org.um.nine.headless.game.domain;

public class Marker implements Cloneable {
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

    @Override
    public Marker clone() {
        try {
            Marker clone = (Marker) super.clone();
            clone.id = this.id;
            clone.setCurrent(this.isCurrent);
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
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
