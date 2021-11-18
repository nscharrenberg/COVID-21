package org.um.nine.v1.domain;

public class InfectionRateMarker extends Marker {
    private int count;
    private int[] draws = {2,2,2,3,3,4,4}; //how many cards you draw based on marker position
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

    public int[] getDraws(){
        return draws;
    }
    public int getCurrentDraw(){
        return draws[count];
    }
    public int getDraw(int pos){
        return draws[pos];
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("infection-rate-marker-");
        sb.append(count);
        sb.append("-");
        sb.append(this.getId());

        return sb.toString();
    }
}
