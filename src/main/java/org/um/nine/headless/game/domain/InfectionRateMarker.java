package org.um.nine.headless.game.domain;

public class InfectionRateMarker extends Marker {
    private static int INCREMENT = 0;
    private int count;
    private int[] draws = {2, 2, 2, 3, 3, 4, 4};

    public InfectionRateMarker(int id, int count) {
        super(id);
        this.count = count;

        INCREMENT++;
    }

    public InfectionRateMarker(int id, boolean isCurrent, int count) {
        super(id, isCurrent);
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
