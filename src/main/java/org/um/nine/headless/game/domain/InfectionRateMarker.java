package org.um.nine.headless.game.domain;

import java.util.Arrays;

public class InfectionRateMarker extends Marker {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InfectionRateMarker that = (InfectionRateMarker) o;
        return count == that.count &&
                Arrays.equals(draws, that.draws);
    }

    @Override
    public InfectionRateMarker clone() {
        InfectionRateMarker clone = (InfectionRateMarker) super.clone();
        clone.count = this.count;
        clone.draws = this.draws;
        return clone;
    }

    private static int INCREMENT = 0;
    private int count;
    private int[] draws = {2, 2, 2, 3, 3, 4, 4};

    public InfectionRateMarker(int count) {
        super(INCREMENT);
        this.count = count;

        INCREMENT++;
    }

    public InfectionRateMarker(int count, boolean isCurrent) {
        super(INCREMENT, isCurrent);
        this.count = count;

        INCREMENT++;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int[] getDraws() {
        return draws;
    }

    public void setDraws(int[] draws) {
        this.draws = draws;
    }

    @Override
    public String toString() {
        return "infection-rate-marker-" +
                count +
                "-" +
                this.getId();
    }
}
