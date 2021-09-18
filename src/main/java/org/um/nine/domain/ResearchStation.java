package org.um.nine.domain;

public class ResearchStation {
    private City location;

    public ResearchStation(City location) {
        this.location = location;
    }

    public City getLocation() {
        return location;
    }

    public void setLocation(City location) {
        this.location = location;
    }
}
