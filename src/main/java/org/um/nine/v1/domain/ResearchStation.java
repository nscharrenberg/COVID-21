package org.um.nine.v1.domain;

public class ResearchStation {
    private static int ID_COUNT = 1;
    private int id;
    private City city;

    public ResearchStation(City location) {
        this.id = ID_COUNT;
        ID_COUNT++;
        this.city = location;

        location.setResearchStation(this);
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("research-station-");
        sb.append(city.getName());
        sb.append("-");
        sb.append(this.id);

        return sb.toString();
    }
}
