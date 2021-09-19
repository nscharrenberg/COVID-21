package org.um.nine.domain;

public class InfectionRateMarker extends Marker {
    private int count;

    public InfectionRateMarker(int id, int count) {
        super(id);
        this.count = count;
    }

    public InfectionRateMarker(int id, int count, boolean isCurrent) {
        super(id, isCurrent);
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}