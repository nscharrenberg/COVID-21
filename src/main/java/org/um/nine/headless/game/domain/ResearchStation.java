package org.um.nine.headless.game.domain;

public class ResearchStation {
    private static int INCREMENT = 0;
    private int id;
    private City city;

    public ResearchStation() {
        this.id = INCREMENT;
        INCREMENT++;
    }

    public ResearchStation(City city) {
        this();
        this.city = city;
    }

    public int getId() {
        return id;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "ResearchStation{" +
                "id=" + id +
                ", city=" + city.getName() + "-" + city.getId() +
                '}';
    }
}
