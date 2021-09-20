package org.um.nine.domain;

public class ResearchStation {
    private City city;

    public ResearchStation(City location) {
        this.city = location;

        location.setResearchStation(this);
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}
