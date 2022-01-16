package org.um.nine.headless.game.domain;

public class ResearchStation implements Cloneable {
    private static int INCREMENT = 0;
    private int id;
    private City city;

    public ResearchStation clone() {
        ResearchStation other;
        try {
            other = (ResearchStation) super.clone();
            other.id = this.id;
            other.city = this.city;
            return other;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

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
